import { ApiProperty } from '@nestjs/swagger';

export class DeviceResponseDto {
  @ApiProperty({ example: 'device-uuid-123' })
  id!: string;

  @ApiProperty({ example: 42 })
  ownerId!: number;

  @ApiProperty({ example: 'Living Room Speaker', required: false })
  name?: string | null;

  @ApiProperty({ example: '2026-07-20T12:34:56.789Z', format: 'date-time' })
  createdAt!: Date;
}

export class DeviceshipResponseDto {
  @ApiProperty({ example: 'device-uuid-123' })
  deviceId!: string;

  @ApiProperty({ example: 17 })
  userId!: number;

  @ApiProperty({ example: true })
  canSeek!: boolean;

  @ApiProperty({ example: true })
  canTogglePlayPause!: boolean;

  @ApiProperty({ example: false })
  canModifyMusic!: boolean;

  @ApiProperty({ example: '2026-07-20T12:34:56.789Z', format: 'date-time' })
  createdAt!: Date;
}

export class AvailableDeviceResponseDto extends DeviceshipResponseDto {
  @ApiProperty({ example: true })
  isOnlineDevice!: boolean;
}
