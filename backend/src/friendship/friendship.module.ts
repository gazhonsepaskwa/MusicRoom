import { forwardRef, Module } from '@nestjs/common';
import { FriendshipService } from './friendship.service';
import { FriendshipController } from './friendship.controller';
import { UsersModule } from '../users/users.module';
import { NotificationsModule } from '../notifications/notifications.module';
import { PrismaService } from '../prisma/prisma.service';

@Module({
  imports: [UsersModule, forwardRef(() => NotificationsModule)],
  providers: [FriendshipService, PrismaService],
  controllers: [FriendshipController],
  exports: [FriendshipService],
})
export class FriendshipModule {}
 