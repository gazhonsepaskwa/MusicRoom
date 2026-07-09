import { forwardRef, Module } from '@nestjs/common';
import { PlaylistsService } from './playlists.service';
import { PlaylistsController } from './playlists.controller';
import { PlaylistsGateway } from './playlists.gateway';
import { AuthModule } from '../auth/auth.module';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [forwardRef(() =>AuthModule), PrismaModule],
  providers: [PlaylistsService, PlaylistsGateway],
  controllers: [PlaylistsController],
  exports: [PlaylistsService]
})
export class PlaylistsModule {}
