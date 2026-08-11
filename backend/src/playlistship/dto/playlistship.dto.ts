import { IsIn, IsInt, IsNotEmpty } from "class-validator"
import { invitationStatus } from "../../../generated/prisma/enums"
import { ApiProperty } from '@nestjs/swagger';


export class PlaylistshipDto {
	@ApiProperty({})
	@IsInt()
	playlistId!: number
	
	@ApiProperty({})
	@IsInt()
	@IsNotEmpty()
	addresseeId!: number
}

export class PlaylistshipAnswerDto {
	@ApiProperty({})
	@IsInt()
	playlistId!: number
	
	@ApiProperty({
		enum: invitationStatus,
		description: 'The decision for the playlist invitation.',
		example: invitationStatus.ACCEPTED,
	})
	@IsIn(Object.values(invitationStatus))
	@IsNotEmpty()
	status!: invitationStatus
}