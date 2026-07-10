import { forwardRef, Module } from '@nestjs/common';
import { FriendshipService } from './friendship.service';
import { FriendshipController } from './friendship.controller';
import { UsersModule } from '../users/users.module';
import { NotificationsModule } from '../notifications/notifications.module';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [UsersModule, forwardRef(() => NotificationsModule), PrismaModule],
  providers: [FriendshipService],
  controllers: [FriendshipController],
  exports: [FriendshipService],
})
export class FriendshipModule {}
 