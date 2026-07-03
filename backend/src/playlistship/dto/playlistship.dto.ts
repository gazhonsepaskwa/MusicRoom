import { IsIn, IsInt } from "class-validator"
import { invitationStatus } from "../../../generated/prisma/enums"

export class PlaylistshipDto {
	@IsInt()
	playlistId!: number
	@IsInt()
	addresseeId!: number
}

export class PlaylistshipAnswerDto {
	@IsInt()
	playlistId!: number
	@IsIn(Object.values(invitationStatus))
	status!: invitationStatus
}