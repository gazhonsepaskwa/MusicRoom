import { IsIn, IsInt } from "class-validator";
import { invitationStatus } from "../../../generated/prisma/enums";
import { ApiProperty } from '@nestjs/swagger';


export class friendRequestDto {
	@ApiProperty({})
	@IsInt()
	receiverId!: number
}

export class friendReqAnswerDto {
	@ApiProperty({})
	@IsInt()
	senderId!: number
	@ApiProperty({
		enum: invitationStatus
	})
	@IsIn(Object.values(invitationStatus))
	answer!: invitationStatus
}