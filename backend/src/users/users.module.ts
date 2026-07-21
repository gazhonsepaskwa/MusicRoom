import { forwardRef, Module } from '@nestjs/common';
import { UsersService } from './users.service';
import { UsersController } from './users.controller';
import { PrismaModule } from '../prisma/prisma.module';
import { PlaylistsModule } from '../playlists/playlists.module';
import { FriendshipModule } from '../friendship/friendship.module';
import { AuthModule } from '../auth/auth.module';

@Module({
  imports: [PrismaModule, PlaylistsModule,
	forwardRef(() =>FriendshipModule),
	forwardRef(() =>AuthModule),
],
  controllers: [UsersController],
  providers: [UsersService],
  exports: [UsersService],
})
export class UsersModule {}
