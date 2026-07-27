import { All, Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { MusicService } from '../music/music.service';
import { AlbumService } from '../album/album.service';
import { ArtistService } from '../artist/artist.service';
import { PlaylistsService } from '../playlists/playlists.service';
import { UsersService } from '../users/users.service';

@Injectable()
export class SearchService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly musicService: MusicService,
    private readonly artistService: ArtistService,
    private readonly albumService: AlbumService,
    private readonly playlistService: PlaylistsService,
    private readonly userService: UsersService,
  ) {}

  async search(
    query: string,
    userId: number,
    types: string[] = ['music', 'artist', 'album', 'playlist', 'user'],
    offset: number = 0,
    limit: number = 10,
  ) {

    const results: any[] = [];

    await this.prisma.$executeRaw`
      SELECT set_limit(0.1);
    `;

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

    if (types.includes('playlist')) {
      const playlistResults = await this.prisma.$queryRaw<
        { id: number; score: number }[]
      >`
        SELECT id, similarity(title, ${query}) AS score
        FROM "playlist"
        WHERE title % ${query}
        AND "isDefault" = false
        AND (
          "isPublic" = true
          OR (
            "isPublic" = false
            AND EXISTS (
              SELECT 1 FROM "playlistship"
              WHERE "playlistship"."playlistId" = "playlist"."id"
                AND "playlistship"."addresseeId" = ${userId}
            )
          )
        );
      `;
      results.push(...playlistResults.map((r) => ({ ...r, type: 'playlist' })));
    }

    if (types.includes('user')) {
      const userResults = await this.prisma.$queryRaw<
        { id: number; score: number }[]
      >`
        SELECT id, similarity(username, ${query}) AS score
        FROM "user"
        WHERE username % ${query};
      `;
      results.push(...userResults.map((r) => ({ ...r, type: 'user' })));
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

      if (result.type === 'playlist') {
        const playlist = await this.playlistService.playlist({ id: result.id });
        AllResults.push(playlist);
      }

      if (result.type === 'user') {
        const user = await this.userService.get_user({ id: result.id });
        AllResults.push(user);
      }
    }

    return AllResults;
  }
}
