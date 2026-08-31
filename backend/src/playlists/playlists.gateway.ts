import {
  WebSocketGateway,
  WebSocketServer,
  SubscribeMessage,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { PlaylistsService } from './playlists.service';
import { JwtService } from '@nestjs/jwt';

@WebSocketGateway(0, {
  cors: {
    origin: '*',
  },
})
export class PlaylistsGateway {
  @WebSocketServer() server!: Server;
  constructor(
    private readonly playlistsService: PlaylistsService,
    private readonly jwtService: JwtService,
  ) {}

  @SubscribeMessage('join_playlist')
  async handleJoinPlaylist(client: Socket, playlistId: String) {
    const playlistIdNum = Number(playlistId);
    if (isNaN(playlistIdNum)) {
      client.emit('app_error', {
        message:
          'Invalid playlist ID provided. Please check the playlist ID and try again.',
      });
      return;
    }
    if (client.rooms.has(`playlist_${playlistId}`)) {
      client.emit('app_error', {
        message: 'Client is already a member of this playlist',
      });
      return;
    }

    const version = await this.playlistsService.canJoinPlaylist(
      playlistIdNum,
      client.data.userId,
    );
    if (version === undefined) {
      client.emit('app_error', {
        message: 'Playlist not found',
      });
      return;
    }
    client.join(`playlist_${playlistId}`);
    this.server.to(`playlist_${playlistId}`).emit('join_playlist', {
      deviceId: client.data.deviceId,
      version: version,
    });

    const playlist = await this.playlistsService.playlist({
      id: playlistIdNum,
    });
    if (!playlist) {
      client.emit('app_error', {
        message: 'Playlist not found',
      });
      return;
    }

    client.emit('playlist_content', {
      playList: playlist,
    });
    console.log(`Client ${client.data.deviceId} joined playlist ${playlistId}`);
  }

  sendRemoveMusic(playlistId: number, songId: number, version: number): void {
    this.server.to(`playlist_${playlistId}`).emit('remove_music', {
      songId: songId,
      version: version,
    });
  }

  sendAddMusic(playlistId: number, songId: number, version: number): void {
    this.server.to(`playlist_${playlistId}`).emit('add_music', {
      songId: songId,
      version: version,
    });
  }

  @SubscribeMessage('move_music')
  async handleMoveMusic(
    client: Socket,
    {
      playlistId,
      oldIndex,
      newIndex,
      version,
    }: {
      playlistId: string;
      oldIndex: number;
      newIndex: number;
      version: number;
    },
  ) {
    console.log('enter move_music');
    const playlistIdNum = Number(playlistId);
    const oldIndexNum = Number(oldIndex);
    const newIndexNum = Number(newIndex);
    if (isNaN(playlistIdNum) || isNaN(oldIndexNum) || isNaN(newIndexNum)) {
      client.emit('app_error', {
        message:
          'Invalid playlist ID, music ID, or index provided. Please check the values and try again.',
      });
      return;
    }
    if (!client.rooms.has(`playlist_${playlistId}`)) {
      client.emit('app_error', {
        message: 'Client is not a member of this playlist',
      });
      return;
    }

    const true_version =
      await this.playlistsService.getPlaylistVersion(playlistIdNum);
    if (version !== true_version) {
      client.emit('app_error', {
        message: `Version mismatch: client version ${version}, server version ${true_version}`,
      });
      return;
    }

    const newVersion = await this.playlistsService.moveMusic(
      playlistIdNum,
      oldIndexNum,
      newIndexNum,
    );

    if (newVersion === undefined) {
      client.emit('app_error', {
        message: 'Failed to move music in playlist',
      });
      return;
    }

    this.server.to(`playlist_${playlistId}`).emit('music_moved', {
      oldIndex: oldIndex,
      newIndex: newIndex,
      version: newVersion,
      deviceId: client.data.deviceId,
      playlistId,
    });
    console.log(
      `Client ${client.id} moved music ${oldIndex} in playlist ${playlistId} to index ${newIndex}`,
    );
  }

  @SubscribeMessage('leave_playlist')
  handleLeavePlaylist(client: Socket, playlistId: String) {
    const playlistIdNum = Number(playlistId);
    if (isNaN(playlistIdNum)) {
      client.emit('app_error', {
        message: 'Invalid playlist ID provided',
      });
      return;
    }
    if (!client.rooms.has(`playlist_${playlistId}`)) {
      client.emit('app_error', {
        message: 'Client is not a member of this playlist',
      });
      return;
    }

    const version = this.playlistsService.getPlaylistVersion(playlistIdNum);
    if (version === undefined) {
      client.emit('app_error', {
        message: 'Failed to leave playlist: playlist not found',
      });
      return;
    }

    client.leave(`playlist_${playlistId}`);
    this.server
      .to(`playlist_${playlistId}`)
      .emit('leave_playlist', { userId: client.data.userId, version: version });
    console.log(`Client ${client.id} left playlist ${playlistId}`);
  }
}
