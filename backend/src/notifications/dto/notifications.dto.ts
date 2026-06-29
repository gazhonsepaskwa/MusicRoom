import { invitationStatus } from "../../../generated/prisma/enums";

export enum NotificationType {
  FRIEND_REQUEST = 'FRIEND_REQUEST',
  PLAYLIST_INVITATION = 'PLAYLIST_INVITATION',
}

export interface NotificationDto {
  type: NotificationType;
  createdAt: Date;
  status: invitationStatus;

  // data needed by frontend
  requesterId?: number;
  playlistId?: number;
}

export class NotificationBodyDto {
	websoketEvent!: string
	FireBaseTitle?: string
	FirebaseMessage?: string
}