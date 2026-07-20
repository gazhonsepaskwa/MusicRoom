import { Module } from '@nestjs/common';
import { DevicesService } from './devices.service';
import { DevicesController } from './devices.controller';
import { DevicesGateway } from './devices.gateway';
import { PrismaModule } from '../prisma/prisma.module';
import { WebsocketsModule } from '../websockets/websockets.module';
import { AuthModule } from '../auth/auth.module';
import { forwardRef } from '@nestjs/common';

@Module({
  providers: [DevicesService, DevicesGateway],
  controllers: [DevicesController],
  imports: [forwardRef(() => WebsocketsModule), forwardRef(() => AuthModule), PrismaModule],
  exports: [DevicesService],
})
export class DevicesModule {}
