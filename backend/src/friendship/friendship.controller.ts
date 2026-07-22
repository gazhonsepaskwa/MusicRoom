import { BadRequestException, Body, Controller, Delete, Get, Post, Query } from '@nestjs/common';
import { FriendshipService } from './friendship.service';
import { friendReqAnswerDto, friendRequestDto } from './dto/friendRequest.dto';
import { invitationStatus } from '../../generated/prisma/enums';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { ApiBody, ApiOkResponse } from '@nestjs/swagger';
import { FriendshipDto, FriendshipResponseDto } from './dto/friendship-response.dto';

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
		await this.friendshipService.sendFriendRequest(userId, friendRequestDto.receiverId);
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
			friendship.status == invitationStatus.REJECTED ? "rejected" : "accepted",
		}
	}

	@ApiOkResponse({ type: [FriendshipDto] })
	@Get('friend-list')
	async getFriendList(@CurrentUser() userId) {
		return await this.friendshipService.getFriendRequests(userId, [invitationStatus.ACCEPTED])
	}

	@ApiOkResponse({ type: FriendshipDto})
	@ApiBody({ type: friendRequestDto })
	@Delete('delete')
	async deleteFriendship(@CurrentUser() userId: number, @Body() data: friendRequestDto) {
		const isFriend = await this.friendshipService.isFriend(userId, data.receiverId);
		if (isFriend !== invitationStatus.ACCEPTED)
			throw new BadRequestException("You can not delete what does not exist", "Friendship does not Exist!");
		await this.friendshipService.deleteFriendship(userId, data.receiverId);
		return {
			message: "Hello darkness my old friend..."
		};
	}
}
