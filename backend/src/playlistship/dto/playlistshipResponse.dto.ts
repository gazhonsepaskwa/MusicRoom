import { ApiProperty } from "@nestjs/swagger";
import { invitationStatus } from "../../../generated/prisma/enums"


export class InvitationResponseDto {
  @ApiProperty({
    example: 2,
    description: 'The user ID of the invitee.',
  })
  addresseeId!: number;

  @ApiProperty({
    example: 1,
    description: 'The playlist ID associated with the invitation.',
  })
  playlistId!: number;

  @ApiProperty({
    enum: invitationStatus,
    example: invitationStatus.ACCEPTED,
    description: 'The current invitation status.',
  })
  status: invitationStatus;

  @ApiProperty({
    example: '2026-07-03T20:43:40.367Z',
    format: 'date-time',
    description: 'Timestamp when the invitation was created.',
  })
  createdAt!: Date;
}

export class InvitationNotification {
  @ApiProperty({
    example: 1,
    description: 'The playlist ID associated with the invitation.',
  })
  playlistId!: number;

  @ApiProperty({
    enum: invitationStatus,
    example: invitationStatus.ACCEPTED,
    description: 'The current invitation status.',
  })
  status: invitationStatus;

  @ApiProperty({
	example: "Some Playlist Name",
	description: 'The name of the playlist invited to'
  })
  playlistName!: string

  @ApiProperty({example: "2026-07-03T20:43:40.367Z"})
  createdAt!: Date
}