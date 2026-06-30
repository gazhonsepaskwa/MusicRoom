import { invitationStatus } from "../../../generated/prisma/enums"

export class PlaylistshipDto {
	playlistId!: number
	addresseeId!: number
}

export class PlaylistshipAnswerDto {
	playlistId!: number
	status!: invitationStatus
}