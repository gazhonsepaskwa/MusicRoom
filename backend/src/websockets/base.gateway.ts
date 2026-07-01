// src/websockets/base.gateway.ts
import {
  OnGatewayConnection,
  OnGatewayDisconnect,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { JwtService } from '@nestjs/jwt';
import { WebSocketsService } from './websockets.service';
import { AuthService } from '../auth/auth.service';

@WebSocketGateway({
  cors: { origin: '*' },
})
export class BaseGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer() server!: Server;

  constructor(
    protected jwtService: JwtService,
    private readonly websocketsService: WebSocketsService,
    private readonly authService: AuthService,
  ) {}

  async handleConnection(client: Socket) {
    try {
      const token = client.handshake.auth?.token;
      console.log('token', token);

      const id = await this.authService.getUserFromJWT(token);
      if (!id) {
        throw new Error('Invaldid token');
      }

      const deviceId = client.handshake.auth?.deviceId;
      console.log('deviceId', deviceId);
      if (!deviceId) {
        throw new Error('Invalid deviceId');
      }

      client.data.userId = id;
      console.log(`Client ${client.id} (userID: ${id}) connected`);
      this.websocketsService.addSocket(id, client.id, deviceId);
    } catch (err) {
      console.log('Auth failed');
      client.disconnect(true);
    }
  }

  handleDisconnect(client: Socket) {
    console.log(
      `Client ${client.id} (userID: ${client.data.userId}) disconnected`,
    );
    this.websocketsService.removeSocket(client.data.userId, client.id);
  }

  sendToUser(userId: number, event: string, data: any) {
    const sockets = this.websocketsService.getUserSockets(userId);
    if (!sockets) return;
    sockets.forEach((socketId) => {
      this.server.to(socketId).emit(event, data);
    });
  }
}
