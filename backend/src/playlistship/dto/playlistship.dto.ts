import { invitationStatus } from "../../../generated/prisma/enums"

export class PlaylistshipDto {
	playlistId!: number
	addresseeId!: number

	status?: invitationStatus
}