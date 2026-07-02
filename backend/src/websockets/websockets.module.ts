import { Module } from '@nestjs/common';
import { BaseGateway } from './base.gateway';
import { WebSocketsService } from './websockets.service';
import { PrismaService } from '../prisma/prisma.service';
import { UsersService } from '../users/users.service';
import { MailService } from '../mail/mail.service';
import { forwardRef } from '@nestjs/common';

import { DevicesModule } from '../devices/devices.module';
import { AuthModule } from '../auth/auth.module';

@Module({
  providers: [
    BaseGateway,
    WebSocketsService,
    PrismaService,
    UsersService,
    MailService,
  ],
  imports: [forwardRef(() => DevicesModule), forwardRef(() => AuthModule)],

  exports: [BaseGateway, WebSocketsService],
})
export class WebsocketsModule {}
