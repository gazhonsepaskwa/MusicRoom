import { Module } from '@nestjs/common';
import { DevicesService } from './devices.service';
import { DevicesController } from './devices.controller';
import { DevicesGateway } from './devices.gateway';
import { PrismaService } from '../prisma/prisma.service';
import { AuthGuard } from '../auth/auth.guard';
import { WebsocketsModule } from '../websockets/websockets.module';
import { WebSocketsService } from '../websockets/websockets.service';

@Module({
  providers: [
    DevicesService,
    DevicesGateway,
    PrismaService,
    AuthGuard,
    WebSocketsService,
  ],
  controllers: [DevicesController],
  imports: [WebsocketsModule],
})
export class DevicesModule {}
