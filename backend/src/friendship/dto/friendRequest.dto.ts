import { IsIn, IsInt } from "class-validator"
import { invitationStatus } from "../../../generated/prisma/enums"

export class friendRequestDto {
	@IsInt()
	receiverId!: number
}

export class friendReqAnswerDto {
	@IsInt()
	senderId!: number
	@IsIn(Object.values(invitationStatus))
	answer!: invitationStatus
}