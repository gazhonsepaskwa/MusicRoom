import { forwardRef, Inject, Injectable } from '@nestjs/common';
import { FriendshipService } from '../friendship/friendship.service';
import {
  NotificationBodyDto,
  NotificationDto,
  NotificationType,
} from './dto/notifications.dto';
import { invitationStatus } from '../../generated/prisma/enums';
import { WebSocketsService } from '../websockets/websockets.service';
import { BaseGateway } from '../websockets/base.gateway';
import { PlaylistshipService } from '../playlistship/playlistship.service';
import { UsersService } from '../users/users.service';
import { FirebaseService } from './firebase/firebase.service';

@Injectable()
export class NotificationsService {
	constructor(
		@Inject(forwardRef(() => PlaylistshipService))
		private playlistshipService: PlaylistshipService,
		 @Inject(forwardRef(() => FriendshipService))
		private friendshipService: FriendshipService,
		private websocketService: WebSocketsService,
		private baseGateway: BaseGateway,
		@Inject(forwardRef(() => UsersService))
		private usersService: UsersService,
		private pushNotification: FirebaseService
	) {}

	async getPendingNotifications(userId: number): Promise<NotificationDto[] | null> {
		let friendshipNotif = await this.friendshipService.getFriendRequests(userId, [invitationStatus.PENDING, invitationStatus.NOTVIEWED]);
		let playlistNotif = await this.playlistshipService.getPlaylistInvitations(userId, [invitationStatus.PENDING, invitationStatus.NOTVIEWED]);
		
		return [
			...friendshipNotif.map((f) => ({
			type: NotificationType.FRIEND_REQUEST,
			createdAt: f.createdAt,
			status: f.status,
			requesterId: f.otherId,
			requesterName: f.otherUsername
			})),

			...playlistNotif.map((p) => ({
			type: NotificationType.PLAYLIST_INVITATION,
			createdAt: p.createdAt,
			status: p.status,
			playlistId: p.playlistId,
			playlistName: p.playlistName
			})),
		].sort(
			(a, b) =>
			b.createdAt.getTime() - a.createdAt.getTime(),
		);
	}

  async sendNotification(
    receiverId: number,
    senderId: number,
    body: NotificationBodyDto,
  ) {
    if (this.websocketService.isOnlineUser(receiverId))
      this.baseGateway.sendToUser(receiverId, body.websoketEvent, {
        From: senderId,
        To: receiverId,
      });
    // A DECOMMENTER LORSQUE FIREBASE EST INSTALLE EN FRONT + USER A FIRBASE TOKEN
    /*else if (body.FireBaseTitle && body.FirebaseMessage){
			const user = await this.usersService.user({id: receiverId});
			if (user){

				this.pushNotification.sendPushNotification(
					[user.FirbaseToken],
					body.FireBaseTitle,
					body.FirebaseMessage
				)
			}
		}*/
  }

  async updateUsersNotification(userId: number) {
    this.friendshipService.updateManpyFriendshipStatus(
      { addresseeId: userId, status: invitationStatus.NOTVIEWED },
      { status: invitationStatus.PENDING },
    );
    this.playlistshipService.updateManpyPlaylistshipStatus(
      { addresseeId: userId, status: invitationStatus.NOTVIEWED },
      { status: invitationStatus.PENDING },
    );
  }
}
