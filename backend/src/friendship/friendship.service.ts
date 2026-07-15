import { BadRequestException, forwardRef, Inject, Injectable, InternalServerErrorException } from '@nestjs/common';
import { friendship, Prisma, invitationStatus } from '../../generated/prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { UsersService } from '../users/users.service';
import { friendReqAnswerDto } from './dto/friendRequest.dto';
import { NotificationsService } from '../notifications/notifications.service';
import { FriendshipDto } from './dto/friendship-response.dto';

@Injectable()
export class FriendshipService {
	constructor(
		private prisma: PrismaService, 
		private usersService: UsersService, 
		@Inject(forwardRef(() => NotificationsService))
		private notificationsService: NotificationsService) {}
	async sendFriendRequest(senderId: number, receiverId: number): Promise<void> {
		if (senderId === receiverId) {
			throw new BadRequestException('Cannot send friend request to yourself');
		}
		if (await this.friendshipExists(senderId, receiverId)) {
			throw new BadRequestException('Friendship already sent');
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
	} 

	async createFriendship(data: Prisma.friendshipUncheckedCreateInput): Promise<friendship> {
		return this.prisma.friendship.create({
			data,
		});
	}

	async friendshipExists(requesterId: number, addresseeId: number): Promise<boolean> {
		try {

			let friendship = await this.prisma.friendship.findUnique({
				where: { requesterId_addresseeId: { requesterId, addresseeId } },
			});
			if (friendship === null) {
				friendship = await this.prisma.friendship.findUnique({
					where: { requesterId_addresseeId: { requesterId: addresseeId, addresseeId: requesterId } },
				});
			}
			return friendship !== null;
		}
		catch (error) {
			console.log(error);
			return false;
		}
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

	async updateManpyFriendshipStatus(where : Prisma.friendshipWhereInput, data: Prisma.friendshipUpdateInput){
		return await this.prisma.friendship.updateMany({
			data,
			where,
		})
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

	async getFriendRequests(userId: number, status?: invitationStatus[]): Promise<FriendshipDto[]> {
		let where: Prisma.friendshipWhereInput;
		if (status?.includes(invitationStatus.ACCEPTED)){
			where = {
				OR: [
					{addresseeId: userId},
					{requesterId: userId},
				]
			}
		}
		else {
			where = {
				addresseeId: userId ,
			};
		}
		if (status?.length) {
			where.status = {
				in: status,
			};
		}
		const friendRequests = await this.prisma.friendship.findMany({
			where,
			include: {
				requester: {
					select: {
						id: true,
						username: true,
					}
				},
				addressee:  {
					select: {
						id: true,
						username: true,
					}
				}
			}
		});

		return friendRequests.map((friendship) => {
			const otherUser = friendship.requesterId == userId ? friendship.addressee : friendship.requester;

			return {
				status: friendship.status,
				otherId: otherUser.id,
				otherUsername: otherUser.username,
				createdAt: friendship.createdAt
			}
		}
		)
	}

	async answerFriendRequest(friendRequestDto: friendReqAnswerDto, receiverId: number): Promise<friendship> {
		if (!friendRequestDto.answer)
			throw new BadRequestException("Answer Needed for Friend Request")
		const status = friendRequestDto.answer ? invitationStatus.ACCEPTED : invitationStatus.REJECTED
		const friendship = await this.updateFriendshipStatus(
			receiverId, 
			friendRequestDto.senderId, 
			status
		)
		if (!friendship)
			throw new InternalServerErrorException("Friendship was not recognized")
		return friendship
	}
}
