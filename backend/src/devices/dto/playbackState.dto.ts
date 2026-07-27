import { IsBoolean, IsInt, IsArray, IsString } from 'class-validator';

export class PlaybackStateDto {
  @IsBoolean()
  isPlaying?: boolean;

  @IsInt()
  currentTime?: number;

  @IsArray()
  @IsInt({ each: true })
  musicListIds?: number[];

  @IsString()
  deviceId!: string;
}
