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
      let token = client.handshake.auth?.token;
	  if (!token)
		token = client.handshake.headers.authorization;
      if (!token) {
        console.log('Missing token');
        client.disconnect(true);
        return;
      } 

	  const payload = await this.jwtService.verifyAsync(token);
      if (!payload.sub) {
        console.log('Invalid token');
        client.disconnect(true);
        return;
      }

      client.data.userId = payload.sub;
      console.log(`Client ${client.id} (userID: ${payload.sub}) connected`);
      this.websocketsService.addSocket((payload.sub).toString(), client.id);
    } catch (err) {
      console.log(err);
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

  sendToUser(userId: string, event: string, data: any) {
    const sockets = this.websocketsService.getUserSockets(userId);
    if (!sockets) return;
    sockets.forEach((socketId) => {
      this.server.to(socketId).emit(event, data);
    });
  }
}
