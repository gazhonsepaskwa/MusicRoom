import { Module } from '@nestjs/common';
import { SearchService } from './search.service';
import { SearchController } from './search.controller';
import { PrismaService } from '../prisma/prisma.service';
import { AlbumService } from '../album/album.service';
import { ArtistService } from '../artist/artist.service';
import { MusicService } from '../music/music.service';

@Module({
  controllers: [SearchController],
  providers: [
    SearchService,
    PrismaService,
    MusicService,
    ArtistService,
    AlbumService,
  ],
})
export class SearchModule {}
