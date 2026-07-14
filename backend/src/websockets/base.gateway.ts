import {
  OnGatewayConnection,
  OnGatewayDisconnect,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { JwtService } from '@nestjs/jwt';
import { WebSocketsService } from './websockets.service';

@WebSocketGateway({
  cors: { origin: '*' },
})
export class BaseGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer() server!: Server;

  constructor(
    protected jwtService: JwtService,
    private readonly websocketsService: WebSocketsService,
  ) {}

  async handleConnection(client: Socket) {
    client.onAny((event, ...args) => {
      console.log('Event reçu:', event, args);
    });
    try {
      let token = client.handshake.auth?.token;
      if (!token) token = client.handshake.headers.authorization;
      console.log(client.handshake);
      if (!token) {
        console.log('Missing token');
        client.emit('app_error', { message: 'Missing token' });
        client.disconnect(true);
        return;
      }

      let device = client.handshake.auth?.device;
      if (!device) device = client.handshake.headers.device;
      if (!device) {
        console.log('Missing device');
        client.emit('app_error', { message: 'Missing device' });
        client.disconnect(true);
        return;
      }
      if (token.startsWith('Bearer ')) {
        token = token.slice(7, token.length);
      }
      const payload = await this.jwtService.verifyAsync(token);
      if (!payload.sub) {
        console.log('Invalid token');
        client.emit('app_error', { message: 'Invalid token' });
        client.disconnect(true);
        return;
      }

      client.data.userId = payload.sub;
      client.data.deviceId = device;
      console.log(`Client ${client.id} (userID: ${payload.sub}) connected`);
      this.websocketsService.addSocket(
        payload.sub.toString(),
        client.id,
        device,
      );
    } catch (err) {
      console.log(err);
      console.log('Auth failed');
      client.emit('app_error', { message: 'Auth failed' });
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

  sendToDevice(deviceId: string, event: string, data: any): boolean {
    const socketId = this.websocketsService.getSocketByDeviceId(deviceId);

    if (!socketId) return false;

    this.server.to(socketId).emit(event, data);

    return true;
  }

  @SubscribeMessage('ping')
  handlePing(client: Socket) {
    console.log(`Ping from ${client.id}`);
    client.emit('pong');
  }
}
