// src/websockets/base.gateway.ts
import {
  OnGatewayConnection,
  OnGatewayDisconnect,
  WebSocketGateway,
} from '@nestjs/websockets';
import { Socket } from 'socket.io';
import { JwtService } from '@nestjs/jwt';

@WebSocketGateway({
  cors: { origin: '*' },
})
export class BaseGateway implements OnGatewayConnection, OnGatewayDisconnect {
  constructor(protected jwtService: JwtService) {}

  async handleConnection(client: Socket) {
    try {
      const token = client.handshake.headers.authorization;
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
    } catch (err) {
      console.log('Auth failed');
      client.disconnect(true);
    }
  }

  handleDisconnect(client: Socket) {
    console.log(
      `Client ${client.id} (userID: ${client.data.userId}) disconnected`,
    );
  }
}
