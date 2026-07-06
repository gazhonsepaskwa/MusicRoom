import { ApiProperty } from '@nestjs/swagger';
import { MusicArtistDto } from '../../music/dto/music.dto';

export class CreatePlaylistDto {
  @ApiProperty({ example: 'My Playlist' })
  title!: string;

  @ApiProperty({ example: 'Random Description' })
  status!: string;

  @ApiProperty({ example: true })
  isPublic!: boolean;
}

export class UpdatePlaylistDto {
  @ApiProperty({ example: 'My Playlist 2.0', required: false })
  title?: string;

  @ApiProperty({ example: 'Updated Random Description', required: false })
  status?: string;

  @ApiProperty({ example: true, required: false })
  isPublic?: boolean;
}

export class PlaylistMusicDetailDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Song Title' })
  title!: string;

  @ApiProperty({ example: 180 })
  duration!: number;

  @ApiProperty({ type: [MusicArtistDto] })
  artists!: MusicArtistDto[];
}

export class PlaylistMusicItemDto {
  @ApiProperty({ example: 0 })
  index!: number;

  @ApiProperty({ type: PlaylistMusicDetailDto })
  music!: PlaylistMusicDetailDto;
}

export class PlaylistUserDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'username' })
  username!: string;
}

export class PlaylistShipDto {
  @ApiProperty({ type: PlaylistUserDto })
  user!: PlaylistUserDto;
}

export class PlaylistDetailResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'My Playlist' })
  title!: string;

  @ApiProperty({ example: true })
  isPublic!: boolean;

  @ApiProperty({ example: false })
  isDefault!: boolean;

  @ApiProperty({ example: 'Random Description' })
  status!: string;

  @ApiProperty({ example: 'playlist' })
  type!: string;

  @ApiProperty({ type: [PlaylistMusicItemDto] })
  musics!: PlaylistMusicItemDto[];

  @ApiProperty({ type: [PlaylistShipDto] })
  playlistships!: PlaylistShipDto[];
}

export class PlaylistResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;
  
	@ApiProperty({ example: true })
	isPublic!: boolean;

  @ApiProperty({ example: 'My Playlist' })
  title!: string;

  @ApiProperty({ example: 1 })
  userId!: number;

  @ApiProperty({ example: false })
  isDefault!: boolean;

  @ApiProperty({ example: 0 })
  version!: number;

  @ApiProperty({ example: 'Random Description' })
  status!: string;

  @ApiProperty({ example: '2026-01-01T00:00:00.000Z' })
  createdAt!: Date;
}
