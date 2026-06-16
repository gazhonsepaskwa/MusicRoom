import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import { Socket } from 'socket.io';

@Injectable()
export class WsAuthMiddleware {
  constructor(private jwtService: JwtService) {}

  async use(client: Socket, next: (err?: Error) => void) {
    try {
      const token = client.handshake.auth?.token;
      //    client.handshake.headers?.authorization?.split(' ')[1];

      if (!token) {
        throw new UnauthorizedException('Token manquant');
      }

      const payload = this.jwtService.verify(token);
      if (!payload.userId) {
        throw new UnauthorizedException('Token invalide');
      }

      client.data.userId = payload.userId;
      next();
    } catch (err) {
      next(new UnauthorizedException('Authentification échouée'));
    }
  }
}
