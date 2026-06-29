import { forwardRef, Module } from '@nestjs/common';
import { NotificationsService } from './notifications.service';
import { NotificationsController } from './notifications.controller';
import { PlaylistshipModule } from '../playlistship/playlistship.module';
import { FriendshipModule } from '../friendship/friendship.module';
import { WebsocketsModule } from '../websockets/websockets.module';
import { BaseGateway } from '../websockets/base.gateway';
import { UsersModule } from '../users/users.module';
import { FirebaseModule } from './firebase/firebase.module';
import { AuthGuard } from '../auth/auth.guard';

@Module({
  imports: [
    forwardRef(() => PlaylistshipModule),
	forwardRef(() => FriendshipModule),
	WebsocketsModule,
	UsersModule,
	FirebaseModule],
  controllers: [NotificationsController],
  providers: [NotificationsService, AuthGuard],
  exports: [NotificationsService],
}) 
export class NotificationsModule {}
