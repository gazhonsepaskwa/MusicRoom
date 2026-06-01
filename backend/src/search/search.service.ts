import { All, Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { MusicService } from '../music/music.service';
import { AlbumService } from '../album/album.service';
import { ArtistService } from '../artist/artist.service';

@Injectable()
export class SearchService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly musicService: MusicService,
    private readonly artistService: ArtistService,
    private readonly albumService: AlbumService,
  ) {}

  async search(
    query: string,
    types: string[] = ['music', 'artist', 'album'],
    offset: number = 0,
    limit: number = 10,
  ) {
    console.log(
      'SearchService.search called with query:',
      query,
      'types:',
      types,
      'offset:',
      offset,
      'limit:',
      limit,
    );
    const results: any[] = [];

    if (types.includes('music')) {
      const musicResults = await this.prisma.$queryRaw<
        { id: number; score: number }[]
      >`
        SELECT id, similarity(title, ${query}) AS score
        FROM "music"
        WHERE title % ${query};
      `;
      results.push(...musicResults.map((r) => ({ ...r, type: 'music' })));
    }

    if (types.includes('artist')) {
      const artistResults = await this.prisma.$queryRaw<
        { id: number; score: number }[]
      >`
        SELECT id, similarity(title, ${query}) AS score
        FROM "artist"
        WHERE title % ${query};
      `;
      results.push(...artistResults.map((r) => ({ ...r, type: 'artist' })));
    }

    if (types.includes('album')) {
      const albumResults = await this.prisma.$queryRaw<
        { id: number; score: number }[]
      >`
        SELECT id, similarity(title, ${query}) AS score
        FROM "album"
        WHERE title % ${query};
      `;
      results.push(...albumResults.map((r) => ({ ...r, type: 'album' })));
    }

    results.sort((a, b) => b.score - a.score);

    const selectedresults = results.splice(offset, limit);

    const AllResults: any[] = [];
    for (const result of selectedresults) {
      if (result.type === 'music') {
        const music = await this.musicService.music({ id: result.id });
        AllResults.push(music);
      }

      if (result.type === 'artist') {
        const artist = await this.artistService.artist({ id: result.id });
        AllResults.push(artist);
      }

      if (result.type === 'album') {
        const album = await this.albumService.album({ id: result.id });
        AllResults.push(album);
      }
    }

    return AllResults;
  }
}
