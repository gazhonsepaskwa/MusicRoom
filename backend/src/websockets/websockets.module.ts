import { Module } from '@nestjs/common';
import { BaseGateway } from './base.gateway';
import { WebSocketsService } from './websockets.service';
import { PrismaService } from '../prisma/prisma.service';
import { JwtModule } from '@nestjs/jwt';
import { AuthModule } from '../auth/auth.module';

@Module({
  imports: [JwtModule, AuthModule],
  providers: [BaseGateway, WebSocketsService, PrismaService],
  exports: [BaseGateway, WebSocketsService],
})
export class WebsocketsModule {}
