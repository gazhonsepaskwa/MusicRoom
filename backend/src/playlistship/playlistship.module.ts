import { forwardRef, Module } from '@nestjs/common';
import { PlaylistshipService } from './playlistship.service';
import { PlaylistshipController } from './playlistship.controller';
import { NotificationsModule } from '../notifications/notifications.module';
import { UsersModule } from '../users/users.module';
import { PlaylistsModule } from '../playlists/playlists.module';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
	imports: [
		PlaylistsModule, 
		forwardRef(() => NotificationsModule),
		forwardRef(() => UsersModule),
		PrismaModule,
],
  controllers: [PlaylistshipController],
  providers: [PlaylistshipService],
  exports: [PlaylistshipService],
})
export class PlaylistshipModule {}
