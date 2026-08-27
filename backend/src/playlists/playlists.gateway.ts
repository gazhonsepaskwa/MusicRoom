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
  handleJoinPlaylist(client: Socket, playlistId: String) {
    const playlistIdNum = Number(playlistId);
    if (isNaN(playlistIdNum)) {
      console.log(
        `Client ${client.data.userId} provided invalid playlist ID: ${playlistId}`,
      );
      return;
    }
    if (client.rooms.has(`playlist_${playlistId}`)) {
      console.log(
        `Client ${client.data.userId} is already a member of playlist ${playlistId}`,
      );
      return;
    }

    const version = this.playlistsService.canJoinPlaylist(
      playlistIdNum,
      client.data.userId,
    );
    if (version === undefined) {
      console.log(`Failed to join playlist ${playlistId}: playlist not found`);
      return;
    }
    client.join(`playlist_${playlistId}`);
    this.server
      .to(`playlist_${playlistId}`)
      .emit('join_playlist', client.data.userId, version);
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
      console.log(
        `Client ${client.id} provided invalid playlist ID or song ID: playlistId=${playlistId}, songId=${songId}`,
      );
      return;
    }
    if (!client.rooms.has(`playlist_${playlistId}`)) {
      console.log(
        `Client ${client.id} cannot add music to playlist ${playlistId} because they are not a member`,
      );
      return;
    }

    const true_version =
      await this.playlistsService.getPlaylistVersion(playlistIdNum);
    if (version !== true_version) {
      console.log(
        `Failed to add music to playlist ${playlistId}: version mismatch (client version ${version}, server version ${true_version})`,
      );
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
      console.log(`Failed to add music ${songId} to playlist ${playlistId}`);
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
      console.log(
        `Client ${client.id} provided invalid playlist ID, music ID, or index: playlistId=${playlistId}, musicId=${musicId}, index=${index}`,
      );
      return;
    }
    if (!client.rooms.has(`playlist_${playlistId}`)) {
      console.log(
        `Client ${client.id} cannot move music in playlist ${playlistId} because they are not a member`,
      );
      return;
    }

    const true_version =
      await this.playlistsService.getPlaylistVersion(playlistIdNum);
    if (version !== true_version) {
      console.log(
        `Version mismatch for playlist ${playlistId}: client version ${version}, server version ${true_version}`,
      );
      return;
    }

    const newVersion = await this.playlistsService.moveMusic(
      playlistIdNum,
      musicIdNum,
      index,
    );

    if (newVersion === undefined) {
      console.log(
        `Failed to move music ${musicId} in playlist ${playlistId} to index ${index}`,
      );
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
      console.log(
        `Client ${client.id} provided invalid playlist ID: ${playlistId}`,
      );
      return;
    }
    if (!client.rooms.has(`playlist_${playlistId}`)) {
      console.log(
        `Client ${client.id} cannot leave playlist ${playlistId} because they are not a member`,
      );
      return;
    }

    const version = this.playlistsService.getPlaylistVersion(playlistIdNum);
    if (version === undefined) {
      console.log(`Failed to leave playlist ${playlistId}: playlist not found`);
      return;
    }

    client.leave(`playlist_${playlistId}`);
    this.server
      .to(`playlist_${playlistId}`)
      .emit('leave_playlist', client.id, version);
    console.log(`Client ${client.id} left playlist ${playlistId}`);
  }
}
