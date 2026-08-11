import { IsBoolean, IsInt, IsArray, IsString } from 'class-validator';
import { MusicDto } from '../../music/dto/music.dto';

export class PlaybackStateDto {
  @IsBoolean()
  isPlaying?: boolean;

  @IsInt()
  currentTime?: number;

  @IsInt()
  currentMusicId?: number;

  @IsArray()
  @IsInt({ each: true })
  musicListIds?: number[];

  @IsString()
  deviceId!: string;
}

export class PlaybackStateResponseDto {
  @IsBoolean()
  isPlaying?: boolean;

  @IsInt()
  currentTime?: number;

  @IsInt()
  currentMusicId?: number;

  musicList?: MusicDto[];

  @IsString()
  deviceId!: string;

  @IsBoolean()
  isAccepted?: boolean;
}

export class PlaybackStateResponseRejectDto {
  @IsString()
  deviceId!: string;

  @IsBoolean()
  isAccepted?: boolean;
}
