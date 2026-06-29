import { Body, Controller, Get, Post, Query } from '@nestjs/common';
import { FriendshipService } from './friendship.service';
import { FirebaseService } from '../notifications/firebase/firebase.service';
import { friendRequestDto } from './dto/friendRequest.dto';
import { invitationStatus } from '../../generated/prisma/enums';

@Controller('friendship')
export class FriendshipController {
	constructor(
		private readonly friendshipService: FriendshipService,
	) {}

	@Post('send-friend-request')
	async sendFriendRequest(@Body() friendRequestDto : friendRequestDto){
		try {
			await this.friendshipService.sendFriendRequest(friendRequestDto.senderId, friendRequestDto.receiverId);
		}
		catch (error){
			return {message: error};
		}
		return {message: "Friend Request Send!"};
	}

	@Post('answer-friend-request')
	async answerFriendRequest(@Body() friendRequestDto : friendRequestDto){
		const friendship = await this.friendshipService.answerFriendRequest(friendRequestDto);
		return {
			message: "Friend request " + friendship.status == invitationStatus.ACCEPTED ? "accepted" : "denied",
		}
	}
}
