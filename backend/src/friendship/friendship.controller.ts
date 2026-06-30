import { Body, Controller, Get, Post, Query } from '@nestjs/common';
import { FriendshipService } from './friendship.service';
import { FirebaseService } from '../notifications/firebase/firebase.service';
import { friendReqAnswerDto, friendRequestDto } from './dto/friendRequest.dto';
import { invitationStatus } from '../../generated/prisma/enums';
import { CurrentUser } from '../common/decorators/current-user.decorator';

@Controller('friendship')
export class FriendshipController {
	constructor(
		private readonly friendshipService: FriendshipService,
	) {}

	@Post('send-friend-request')
	async sendFriendRequest(
		@CurrentUser() userId: number,
		@Body() friendRequestDto : friendRequestDto){
		console.log(userId, typeof(userId))
		try {
			await this.friendshipService.sendFriendRequest(userId, friendRequestDto.receiverId);
		}
		catch (error){
			return {message: error};
		}
		return {message: "Friend Request Send!"};
	}

	@Post('answer-friend-request')
	async answerFriendRequest(
		@CurrentUser() userId: number,
		@Body() friendRequestDto : friendReqAnswerDto){
		const friendship = await this.friendshipService.answerFriendRequest(friendRequestDto, userId);
		return {
			message: "Friend request " + friendship.status == 
			invitationStatus.ACCEPTED ? "accepted" : 
			friendship.status == invitationStatus.REJECTED ? "rejected" : "pending",
		}
	}
}
