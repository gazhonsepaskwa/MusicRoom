import {
  WebSocketGateway,
  WebSocketServer,
  SubscribeMessage,
  OnGatewayConnection,
  OnGatewayDisconnect,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { PlaylistsService } from './playlists.service';
import { Public } from '../auth/auth.guard';

@Public()
@WebSocketGateway(0, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST'],
  },
})
export class PlaylistsGateway
  implements OnGatewayConnection, OnGatewayDisconnect
{
  @WebSocketServer() server!: Server;
  constructor(private readonly playlistsService: PlaylistsService) {}

  @Public()
  handleConnection(client: Socket) {
    console.log('🟢 CONNECTED:', client.id);
  }

  @Public()
  handleDisconnect(client: Socket) {
    console.log('🔴 DISCONNECTED:', client.id);
  }

  notifyPlaylistUpdate(playlistId: string) {
    this.server
      .to(`playlist_${playlistId}`)
      .emit('playlist_updated', playlistId);
  }

  @Public()
  @SubscribeMessage('join_playlist')
  handleJoinPlaylist(client: Socket, playlistId: string) {
    client.join(`playlist_${playlistId}`);
    console.log(`Client ${client.id} joined playlist ${playlistId}`);
  }

  @SubscribeMessage('move_music')
  async handleMoveMusic(
    client: Socket,
    {
      playlistId,
      musicId,
      index,
      version,
    }: { playlistId: number; musicId: number; index: number; version: number },
  ) {
    const true_version =
      await this.playlistsService.getPlaylistVersion(playlistId);
    if (version !== true_version) {
      console.log(
        `Version mismatch for playlist ${playlistId}: client version ${version}, server version ${true_version}`,
      );
      return;
    }

    const newVersion = await this.playlistsService.moveMusic(
      playlistId,
      musicId,
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
  handleLeavePlaylist(client: Socket, playlistId: string) {
    client.leave(`playlist_${playlistId}`);
    console.log(`Client ${client.id} left playlist ${playlistId}`);
  }
}
