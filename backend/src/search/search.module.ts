import { Module } from '@nestjs/common';
import { SearchService } from './search.service';
import { SearchController } from './search.controller';
import { PrismaService } from '../prisma/prisma.service';
import { AlbumService } from '../album/album.service';
import { ArtistService } from '../artist/artist.service';
import { MusicService } from '../music/music.service';
import { PlaylistsService } from '../playlists/playlists.service';
import { UsersService } from '../users/users.service';
import { AuthGuard } from '../auth/auth.guard';
import { AuthModule } from '../auth/auth.module';

@Module({
  imports: [AuthModule],
  controllers: [SearchController],
  providers: [
    SearchService,
    PrismaService,
    MusicService,
    ArtistService,
    AlbumService,
    PlaylistsService,
    UsersService,
    AuthGuard,
  ],
})
export class SearchModule {}
