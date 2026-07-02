import { Module } from '@nestjs/common';
import { BaseGateway } from './base.gateway';
import { WebSocketsService } from './websockets.service';
import { PrismaService } from '../prisma/prisma.service';
import { JwtModule } from '@nestjs/jwt';
import { AuthModule } from '../auth/auth.module';
import { jwtConstants } from '../auth/constant';

@Module({
  imports: [
    JwtModule.register({
      global: true,
      secret: jwtConstants.secret,
      signOptions: { expiresIn: '31day' },
    }),
    AuthModule,
  ],
  providers: [BaseGateway, WebSocketsService, PrismaService],
  exports: [BaseGateway, WebSocketsService],
})
export class WebsocketsModule {}
