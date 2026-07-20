import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsNumber } from 'class-validator';

export class UpdateNameDto {
  @ApiProperty({ example: 'device-uuid-123', description: 'Unique ID of the device to rename.' })
  @IsString()
  id!: string;

  @ApiProperty({ example: 'Bedroom Speaker', description: 'New display name for the device.' })
  @IsString()
  name!: string;
}
