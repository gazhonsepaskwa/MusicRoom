import { IsIn, IsInt, IsNotEmpty } from "class-validator";
import { invitationStatus } from "../../../generated/prisma/enums";
import { ApiProperty } from '@nestjs/swagger';


export class friendRequestDto {
	@ApiProperty({})
	@IsInt()
	@IsNotEmpty()
	receiverId!: number
}

export class friendReqAnswerDto {
	@ApiProperty({})
	@IsInt()
	@IsNotEmpty()
	senderId!: number
	@ApiProperty({
		enum: invitationStatus
	})
	@IsIn(Object.values(invitationStatus))
	@IsNotEmpty()
	answer!: invitationStatus
}