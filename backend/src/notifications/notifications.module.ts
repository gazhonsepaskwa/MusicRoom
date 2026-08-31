import { forwardRef, Module } from '@nestjs/common';
import { NotificationsService } from './notifications.service';
import { NotificationsController } from './notifications.controller';
import { PlaylistshipModule } from '../playlistship/playlistship.module';
import { FriendshipModule } from '../friendship/friendship.module';
import { WebsocketsModule } from '../websockets/websockets.module';
import { UsersModule } from '../users/users.module';
import { FirebaseModule } from './firebase/firebase.module';

@Module({
  imports: [
    forwardRef(() => PlaylistshipModule),
	forwardRef(() => FriendshipModule),
	forwardRef(() => UsersModule),
	FirebaseModule,
	WebsocketsModule,
],
  controllers: [NotificationsController],
  providers: [NotificationsService],
  exports: [NotificationsService],
}) 
export class NotificationsModule {}
