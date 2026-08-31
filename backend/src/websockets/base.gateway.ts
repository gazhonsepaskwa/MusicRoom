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
      if (!token) {
        throw new Error('Token not found');
      }

      const id = (await this.jwtService.verifyAsync(token))!.sub;
      if (!id) {
        throw new Error('Invaldid token');
      }

      let device = client.handshake.auth?.device;
      if (!device) device = client.handshake.headers.device;
      if (!device) {
        throw new Error('Device not found');
      }
      client.data.userId = id;
      client.data.deviceId = device;

      console.log(`Client ${client.id} (userID: ${id}) connected`);
      this.websocketsService.addSocket(id, client.id, device);
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
