import { Module } from '@nestjs/common';
import { BaseGateway } from './base.gateway';
import { WebSocketsService } from './websockets.service';
import { PrismaService } from '../prisma/prisma.service';
import { AuthService } from '../auth/auth.service';
import { UsersService } from '../users/users.service';
import { MailService } from '../mail/mail.service';
import { DevicesService } from '../devices/devices.service';

@Module({
  providers: [
    BaseGateway,
    WebSocketsService,
    PrismaService,
    AuthService,
    UsersService,
    MailService,
    DevicesService,
  ],
  exports: [BaseGateway],
})
export class WebsocketsModule {}
