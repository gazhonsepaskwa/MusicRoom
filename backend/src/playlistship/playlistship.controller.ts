import { Body, Controller, Get, Param, Post } from '@nestjs/common';
import { PlaylistshipService } from './playlistship.service';
import { PlaylistshipAnswerDto, PlaylistshipDto } from './dto/playlistship.dto';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { ApiBody, ApiOkResponse } from '@nestjs/swagger';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import { InvitationResponseDto } from './dto/playlistshipResponse.dto';

@Controller('playlistship')
export class PlaylistshipController {
  constructor(private readonly playlistshipService: PlaylistshipService) {}

  @ApiBody({ type: PlaylistshipDto })
  @ApiOkResponse({
	  description: 'Send Playlist invitation. Return Nothing on Success',
  })
  @Post('send-playlist-invitation')
  async sendInvitation(@Body() playlistshipDto: PlaylistshipDto, @CurrentUser() userId: number) {
	return await this.playlistshipService.sendPlaylistInvitation(
		playlistshipDto,
		userId
	);
  }

  @ApiBody({ type: PlaylistshipAnswerDto })
  @ApiOkResponse({
	  description: 'Answer Playlist invitation. Return the updated invitation.',
	  type: InvitationResponseDto,
  })
  @Post('answer-playlist-invitation')
  async answerInvitation(@Body() playlistshipDto: PlaylistshipAnswerDto, @CurrentUser() userId: number){
	return await this.playlistshipService.answerPlaylistInvitation(playlistshipDto, userId);
  }

  @ApiBody({ type: PlaylistshipDto })
  @ApiOkResponse({
	  description: 'Leave a playlist. Returns no content on success.',
  })
  @Post('leave-playlist')
  async leavePlaylist(@Body() playlistshipDto: PlaylistshipDto, @CurrentUser() userId: number) {
	return await this.playlistshipService.deletePlaylistship(playlistshipDto, userId);
  }

	@ApiOkResponse({
		description: 'List of invitations.',
		type: InvitationResponseDto,
		isArray: true,
	})
  @Get('allowed-playlist-users/:id')
  async getPlaylistUsers(@Param('id', ParseSafeIntPipe) id: number){
	return await this.playlistshipService.getPlaylistUsers(id);
  }
}
