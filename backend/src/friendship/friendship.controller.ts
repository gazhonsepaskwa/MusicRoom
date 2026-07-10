import { Body, Controller, Get, Post, Query } from '@nestjs/common';
import { FriendshipService } from './friendship.service';
import { friendReqAnswerDto, friendRequestDto } from './dto/friendRequest.dto';
import { invitationStatus } from '../../generated/prisma/enums';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { ApiBody, ApiOkResponse } from '@nestjs/swagger';
import { FriendshipItemDto, FriendshipResponseDto } from './dto/friendship-response.dto';

@Controller('friendship')
export class FriendshipController {
	constructor(
		private readonly friendshipService: FriendshipService,
	) {}

	@ApiBody({ type: friendRequestDto })
	@ApiOkResponse({ type: FriendshipResponseDto })
	@Post('send-friend-request')
	async sendFriendRequest(
		@CurrentUser() userId: number,
		@Body() friendRequestDto : friendRequestDto){
		try {
			await this.friendshipService.sendFriendRequest(userId, friendRequestDto.receiverId);
		}
		catch (error){
			return {message: error};
		}
		return {message: "Friend Request Send!"};
	}

	@ApiBody({ type: friendReqAnswerDto })
	@ApiOkResponse({ type: FriendshipResponseDto })
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

	@ApiOkResponse({ type: [FriendshipItemDto] })
	@Get('friend-list')
	async getFriendList(@CurrentUser() userId) {
		return await this.friendshipService.getFriendRequests(userId, [invitationStatus.ACCEPTED])
	}
}
