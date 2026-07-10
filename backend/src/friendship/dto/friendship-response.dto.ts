import { ApiProperty } from '@nestjs/swagger';
import { invitationStatus } from '../../../generated/prisma/enums';


export class FriendshipResponseDto {
  @ApiProperty({ example: 'Friend Request Send!' })
  message!: string;
}

export class FriendshipItemDto {
	@ApiProperty({example: 1})
	requesterId!: number
	@ApiProperty({example: 2})
	addresseeId!: number
	@ApiProperty({
		enum: invitationStatus,
		description: 'The status of the friendship request',
		example: invitationStatus.ACCEPTED,
	})
	status!: invitationStatus
	@ApiProperty({example: "2026-07-10T22:32:27.254Z"})
	createdAt!: Date
}
