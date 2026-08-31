import { ApiProperty } from '@nestjs/swagger';

export class ArtistAlbumDto {
  @ApiProperty({ example: 'Album Title' })
  title!: string;

  @ApiProperty({ example: '2024-01-01' })
  date!: string;

  @ApiProperty({ example: [], description: 'Album image URLs.' })
  images!: unknown[];
}

export class ArtistResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Artist Name' })
  title!: string;

  @ApiProperty({ example: [], description: 'Artist image URLs.' })
  images!: unknown[];

  @ApiProperty({ type: [ArtistAlbumDto] })
  albums!: ArtistAlbumDto[];
}

export class ArtistMusicDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Song Title' })
  title!: string;

  @ApiProperty({ example: 180 })
  duration!: number;
}

export class ArtistMusicsResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Artist Name' })
  title!: string;

  @ApiProperty({ example: [], description: 'Artist image URLs.' })
  images!: unknown[];

  @ApiProperty({ type: [ArtistMusicDto] })
  musics!: ArtistMusicDto[];
}
