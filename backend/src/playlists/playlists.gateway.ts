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
      userId: client.data.userId,
      version: version,
    });
    console.log(`Client ${client.data.userId} joined playlist ${playlistId}`);
  }

  @SubscribeMessage('add_music')
  async handleAddMusic(
    client: Socket,
    {
      playlistId,
      songId,
      version,
    }: { playlistId: string; songId: string; version: number },
  ) {
    const playlistIdNum = Number(playlistId);
    const songIdNum = Number(songId);
    if (isNaN(playlistIdNum) || isNaN(songIdNum)) {
      client.emit('app_error', {
        message: 'Invalid playlist ID or song ID provided',
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
    console.log('playlist version before adding music:', version);

    let newVersion: number | void;
    try {
      newVersion = (
        await this.playlistsService.addMusic(playlistIdNum, songIdNum)
      )?.version;
    } catch (error) {
      throw error;
    }
    if (newVersion === undefined) {
      client.emit('app_error', {
        message: 'Failed to add music to playlist',
      });
      return;
    }

    console.log('playlist version after adding music:', newVersion);
    this.server
      .to(`playlist_${playlistId}`)
      .emit('music_added', { songId, version: newVersion, playlistId });
    console.log(
      `Client ${client.id} added music ${songId} to playlist ${playlistId}`,
    );
  }

  @SubscribeMessage('move_music')
  async handleMoveMusic(
    client: Socket,
    {
      playlistId,
      musicId,
      index,
      version,
    }: { playlistId: string; musicId: string; index: number; version: number },
  ) {
    console.log('enter move_music');
    const playlistIdNum = Number(playlistId);
    const musicIdNum = Number(musicId);
    if (isNaN(playlistIdNum) || isNaN(musicIdNum) || isNaN(index)) {
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
      musicIdNum,
      index,
    );

    if (newVersion === undefined) {
      client.emit('app_error', {
        message: 'Failed to move music in playlist',
      });
      return;
    }

    this.server
      .to(`playlist_${playlistId}`)
      .emit('music_moved', { musicId, index, version: newVersion, playlistId });
    console.log(
      `Client ${client.id} moved music ${musicId} in playlist ${playlistId} to index ${index}`,
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
