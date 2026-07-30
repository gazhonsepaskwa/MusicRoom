import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsBoolean, IsNumber, IsNotEmpty, IsOptional } from 'class-validator';

export class UpdatePermissionDto {
  @ApiProperty({ example: 'device-uuid-123', description: 'Unique ID of the device.' })
  @IsString()
  @IsNotEmpty()
  id!: string;

  @ApiProperty({ example: 17, description: 'Friend user ID receiving the permission update.' })
  @IsNumber()
  @IsNotEmpty()
  friendId!: number;

  @ApiProperty({ example: true, required: false, description: 'Permission to seek on the device.' })
  @IsOptional()
  @IsBoolean()
  canSeek?: boolean;

  @ApiProperty({ example: true, required: false, description: 'Permission to toggle play/pause on the device.' })
  @IsOptional()
  @IsBoolean()
  canTogglePlayPause?: boolean;

  @ApiProperty({ example: false, required: false, description: 'Permission to modify music on the device.' })
  @IsOptional()
  @IsBoolean()
  canModifyMusic?: boolean;
}
