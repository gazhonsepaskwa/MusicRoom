import { ApiProperty } from '@nestjs/swagger';
import { invitationStatus } from '../../../generated/prisma/enums';

export class NotificationResponseDto {
  @ApiProperty({ example: 'FRIEND_REQUEST' })
  type!: string;

  @ApiProperty({ example: '2026-07-03T20:43:40.367Z', format: 'date-time' })
  createdAt!: Date;

  @ApiProperty({ enum: invitationStatus, example: invitationStatus.PENDING })
  status!: invitationStatus;

  @ApiProperty({ example: 2, required: false })
  requesterId?: number;

  @ApiProperty({ example: 1, required: false })
  playlistId?: number;
}
