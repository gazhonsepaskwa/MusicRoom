import { ApiProperty } from '@nestjs/swagger';
import { invitationStatus } from '../../../generated/prisma/client';


export class FriendshipResponseDto {
  @ApiProperty({ example: 'Friend Request Send!' })
  message!: string;
}

export class FriendshipDto {
	@ApiProperty({example: 42})
	id!: number

	@ApiProperty({
		enum: invitationStatus,
		example: invitationStatus.ACCEPTED,
	})
	status!: invitationStatus

	@ApiProperty({example: 19})
	otherId!: number

	@ApiProperty({example: "Chuck Yesrris"})
	otherUsername!: string
}