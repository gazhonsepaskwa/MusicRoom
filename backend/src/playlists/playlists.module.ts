import { Module } from '@nestjs/common';
import { PlaylistsService } from './playlists.service';
import { PlaylistsController } from './playlists.controller';
import { PrismaService } from '../prisma/prisma.service';
import { AuthGuard } from '../auth/auth.guard';

@Module({
  providers: [PlaylistsService, PrismaService, AuthGuard],
  controllers: [PlaylistsController],
})
export class PlaylistsModule {}
