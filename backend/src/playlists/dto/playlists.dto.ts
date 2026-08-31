import { ApiProperty } from '@nestjs/swagger';
import { MusicArtistDto } from '../../music/dto/music.dto';
import { IsString, IsNotEmpty, IsBoolean, IsOptional } from 'class-validator';

export class MusicPlaylistDto {
  @ApiProperty({ example: 1 })
  musicId!: number;

  @ApiProperty({ example: 1 })
  playlistId!: number;

  @ApiProperty({ example: 1 })
  version?: number;
}

export class PlaylistListItemDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'My Playlist' })
  title!: string;

  @ApiProperty({ example: 5 })
  songs!: number;

  @ApiProperty({ example: 180000 })
  duration!: number;
}

export class CreatePlaylistDto {
  @ApiProperty({ example: 'My Playlist' })
  @IsString()
  @IsNotEmpty()
  title!: string;

  @IsString()
  @IsNotEmpty()
  @ApiProperty({ example: 'Random Description' })
  status!: string;

  @IsBoolean()
  @IsNotEmpty()
  @ApiProperty({ example: true })
  isPublic!: boolean;
}

export class UpdatePlaylistDto {
  @ApiProperty({ example: 'My Playlist 2.0', required: false })
  @IsOptional()
  @IsString()
  @IsNotEmpty()
  title?: string;

  @ApiProperty({ example: 'Updated Random Description', required: false })
  @IsOptional()
  @IsString()
  @IsNotEmpty()
  status?: string;

  @ApiProperty({ example: true, required: false })
  @IsOptional()
  @IsBoolean()
  isPublic?: boolean;
}

export class PlaylistMusicAlbumDto {
  @ApiProperty({ example: 'Album Title' })
  title!: string;

  @ApiProperty({ example: '2024-01-01' })
  date!: string;

  @ApiProperty({ example: [], description: 'Album image URLs.' })
  images!: string[];
}

export class PlaylistMusicDetailDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Song Title' })
  title!: string;

  @ApiProperty({ example: 180 })
  duration!: number;

  @ApiProperty({ type: PlaylistMusicAlbumDto })
  album!: PlaylistMusicAlbumDto;

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

export class PlaylistVersionResponseDto {
  @ApiProperty({ example: 1 })
  playlistId!: number;

  @ApiProperty({ example: 1 })
  musicId!: number;

  @ApiProperty({ example: 1 })
  version!: number;
}
