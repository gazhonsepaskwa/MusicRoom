import { ApiProperty } from '@nestjs/swagger';

export class AlbumArtistDto {
  @ApiProperty({ example: 'Artist Name' })
  title!: string;
}

export class AlbumMusicDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Song Title' })
  title!: string;

  @ApiProperty({ example: 180 })
  duration!: number;

  @ApiProperty({ type: [AlbumArtistDto] })
  artists!: AlbumArtistDto[];
}

export class AlbumResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Greatest Hits' })
  title!: string;

  @ApiProperty({ example: '2024-01-01', description: 'Release date of the album.' })
  date!: string;

  @ApiProperty({ example: [], description: 'Album image URLs.' })
  images!: unknown[];

  @ApiProperty({ type: [AlbumMusicDto] })
  music!: AlbumMusicDto[];

  @ApiProperty({ type: [AlbumArtistDto] })
  artists!: AlbumArtistDto[];

  @ApiProperty({ example: 'album' })
  type!: string;
}
