import { invitationStatus } from "../../../generated/prisma/enums"

export class friendRequestDto {
	receiverId!: number
}

export class friendReqAnswerDto {
	senderId!: number
	answer!: invitationStatus
}