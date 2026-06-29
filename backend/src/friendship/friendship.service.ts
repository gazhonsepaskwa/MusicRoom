import { BadRequestException, forwardRef, Inject, Injectable, InternalServerErrorException } from '@nestjs/common';
import { friendship, Prisma, invitationStatus } from '../../generated/prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { request } from 'http';
import { UsersService } from '../users/users.service';
import { friendRequestDto } from './dto/friendRequest.dto';
import { NotificationsService } from '../notifications/notifications.service';

@Injectable()
export class FriendshipService {
	constructor(
		private prisma: PrismaService, 
		private usersService: UsersService, 
		@Inject(forwardRef(() => NotificationsService))
		private notificationsService: NotificationsService
	) {}
	async sendFriendRequest(senderId: number, receiverId: number): Promise<void> {
		if (senderId === receiverId) {
			throw new Error('Cannot send friend request to yourself');
		}
		if (await this.friendshipExists(senderId, receiverId)) {
			throw new Error('Already Friend with them');
		}
		this.createFriendship({
			requesterId: senderId,
			addresseeId: receiverId,
		})
		const senderName = await this.usersService.user({id: senderId});
		this.notificationsService.sendNotification(
			receiverId,
			senderId,
			{
				websoketEvent: "friend-request",
				FireBaseTitle: `Friend Request From ${senderName}`,
				FirebaseMessage: `${senderName} wish to connect with you. Will you accept it? This message will self-Destruct in 3... 2... 1...`,
			}
		)
		console.log(`Friend request sent from ${senderId} to ${receiverId}`);
	}

	async createFriendship(data: Prisma.friendshipUncheckedCreateInput): Promise<friendship> {
		console.log(`Friend added between ${data.requesterId} and ${data.addresseeId}`);
		return this.prisma.friendship.create({
			data,
		});
	}

	async friendshipExists(requesterId: number, addresseeId: number): Promise<boolean> {
		let friendship = await this.prisma.friendship.findUnique({
			where: { requesterId_addresseeId: { requesterId, addresseeId } },
		});
		if (friendship == null) {
			friendship = await this.prisma.friendship.findUnique({
				where: { requesterId_addresseeId: { addresseeId,requesterId } },
		});
		}
		return friendship !== null;
	}

	async updateFriendshipStatus(
		requesterId: number,
		addresseeId: number,
		status: invitationStatus,
	): Promise<friendship> {
		return this.prisma.friendship.update({
			where: { requesterId_addresseeId: { requesterId, addresseeId } },
			data: { status },
		});
	}

	async deleteFriendship(id1: number, id2: number) {
		await this.prisma.friendship.deleteMany({
			where: {
				OR: [
				{
					requesterId: id1,
					addresseeId: id2,
				},
				{
					requesterId: id2,
					addresseeId: id1,
				},
				],
			},
		});
	}

	async getFriendRequests(userId: number, status?: invitationStatus[]): Promise<friendship[]> {
		const where: Prisma.friendshipWhereInput = {
			OR: [
					{ addresseeId: userId },
					{ requesterId: userId },
				],
		};

		if (status?.length) {
			where.status = {
				in: status,
			};
		}
		return this.prisma.friendship.findMany({
			where,
		});
	}

	async answerFriendRequest(friendRequestDto: friendRequestDto): Promise<friendship> {
		if (!friendRequestDto.answer)
			throw new BadRequestException("Answer Needed for Friend Request")
		const status = friendRequestDto.answer ? invitationStatus.ACCEPTED : invitationStatus.REJECTED
		const friendship = await this.updateFriendshipStatus(
			friendRequestDto.receiverId, 
			friendRequestDto.senderId, 
			status
		)
		if (!friendship)
			throw new InternalServerErrorException("Friendship was not recognized")
		return friendship
	}
}
