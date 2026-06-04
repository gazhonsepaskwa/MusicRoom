import { Module } from '@nestjs/common';
import { SearchService } from './search.service';
import { SearchController } from './search.controller';
import { PrismaService } from '../prisma/prisma.service';
import { AlbumService } from '../album/album.service';
import { ArtistService } from '../artist/artist.service';
import { MusicService } from '../music/music.service';
import { PlaylistsService } from '../playlists/playlists.service';
import { AuthService } from '../auth/auth.service';
import { UsersService } from '../users/users.service';
import { AuthGuard } from '../auth/auth.guard';

@Module({
  controllers: [SearchController],
  providers: [
    SearchService,
    PrismaService,
    MusicService,
    ArtistService,
    AlbumService,
    PlaylistsService,
    AuthService,
    UsersService,
    AuthGuard,
  ],
})
export class SearchModule {}
