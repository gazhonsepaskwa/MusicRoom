import { Module } from '@nestjs/common';
import { PlaylistsService } from './playlists.service';
import { PlaylistsController } from './playlists.controller';
import { PrismaService } from '../prisma/prisma.service';
import { AuthGuard } from '../auth/auth.guard';
import { PlaylistsGateway } from './playlists.gateway';

@Module({
  providers: [PlaylistsService, PrismaService, AuthGuard, PlaylistsGateway],
  controllers: [PlaylistsController],
})
export class PlaylistsModule {}
