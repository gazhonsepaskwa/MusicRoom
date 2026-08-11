import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsNotEmpty } from 'class-validator';

export class UpdateNameDto {
  @ApiProperty({ example: 'device-uuid-123', description: 'Unique ID of the device to rename.' })
  @IsString()
  @IsNotEmpty()
  id!: string;

  @ApiProperty({ example: 'Bedroom Speaker', description: 'New display name for the device.' })
  @IsString()
  @IsNotEmpty()
  name!: string;
}
