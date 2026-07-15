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

export class FriendshipDto {
	@ApiProperty({
		enum: invitationStatus,
		example: invitationStatus.ACCEPTED,
	})
	status!: invitationStatus

	@ApiProperty({example: 19})
	otherId!: number

	@ApiProperty({example: "Chuck Yesrris"})
	otherUsername!: string

	@ApiProperty({example: "2026-07-03T20:43:40.367Z"})
	createdAt!: Date
}