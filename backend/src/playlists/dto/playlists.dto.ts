import { ApiProperty } from '@nestjs/swagger';

export class CreatePlaylistDto {
  @ApiProperty({ example: 'My Playlist' })
  title!: string;

  @ApiProperty({ example: 'PRIVATE' })
  status!: string;

  @ApiProperty({ example: true })
  isPublic!: boolean;
}

export class UpdatePlaylistDto {
  @ApiProperty({ example: 'My Playlist', required: false })
  title?: string;

  @ApiProperty({ example: 'PRIVATE', required: false })
  status?: string;

  @ApiProperty({ example: true, required: false })
  isPublic?: boolean;
}

export class PlaylistResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'My Playlist' })
  title!: string;

  @ApiProperty({ example: true })
  isPublic!: boolean;

  @ApiProperty({ example: false })
  isDefault!: boolean;

  @ApiProperty({ example: 'PRIVATE' })
  status!: string;

  @ApiProperty({ example: 'playlist' })
  type!: string;
}
