import { IsString, IsBoolean, IsNumber } from 'class-validator';

export class UpdatePermissionDto {
  @IsString()
  id!: string;

  @IsNumber()
  friendId!: number;

  @IsBoolean()
  canSeek?: boolean;

  @IsBoolean()
  canTogglePlayPause?: boolean;

  @IsBoolean()
  canModifyMusic?: boolean;
}
