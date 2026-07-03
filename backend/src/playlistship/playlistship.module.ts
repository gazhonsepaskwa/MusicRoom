import { forwardRef, Module } from '@nestjs/common';
import { PlaylistshipService } from './playlistship.service';
import { PlaylistshipController } from './playlistship.controller';
import { PrismaService } from '../prisma/prisma.service';
import { NotificationsModule } from '../notifications/notifications.module';
import { UsersModule } from '../users/users.module';
import { PlaylistsModule } from '../playlists/playlists.module';

@Module({
  imports: [UsersModule,
	PlaylistsModule, forwardRef(() => NotificationsModule)],
  controllers: [PlaylistshipController],
  providers: [PlaylistshipService,
	PrismaService],
  exports: [PlaylistshipService],
})
export class PlaylistshipModule {}
