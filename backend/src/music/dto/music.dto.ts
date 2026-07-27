import { ApiProperty } from '@nestjs/swagger';

export class MusicAlbumDto {
  @ApiProperty({ example: 'Album Title' })
  title!: string;

  @ApiProperty({ example: '2024-01-01' })
  date!: string;

  @ApiProperty({ example: [], description: 'Album image URLs.' })
  images!: unknown[];
}

export class MusicArtistDto {
  @ApiProperty({ example: 'Artist Name' })
  title!: string;
}

export class MusicResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Song Title' })
  title!: string;

  @ApiProperty({ example: 180 })
  duration!: number;

  @ApiProperty({ type: MusicAlbumDto })
  album!: MusicAlbumDto;

  @ApiProperty({ type: [MusicArtistDto] })
  artists!: MusicArtistDto[];

  @ApiProperty({ example: 'music' })
  type!: string;
}
