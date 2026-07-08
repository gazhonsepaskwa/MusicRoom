import { Body, Controller, Get, Post, Query } from '@nestjs/common';
import { PlaylistshipService } from './playlistship.service';
import { PlaylistshipAnswerDto, PlaylistshipDto } from './dto/playlistship.dto';
import { CurrentUser } from '../common/decorators/current-user.decorator';

@Controller('playlistship')
export class PlaylistshipController {
  constructor(private readonly playlistshipService: PlaylistshipService) {}

  @Post('send-playlist-invitation')
  async sendInvitation(@Body() playlistshipDto: PlaylistshipDto, @CurrentUser() userId: number) {
	return await this.playlistshipService.sendPlaylistInvitation(
		playlistshipDto,
		userId
	);
  }

  @Post('answer-playlist-invitation')
  async answerInvitation(@Body() playlistshipDto: PlaylistshipAnswerDto, @CurrentUser() userId: number){
	return await this.playlistshipService.answerPlaylistInvitation(playlistshipDto, userId);
  }

  @Post('leave-playlist')
  async leavePlaylist(@Body() playlistshipDto: PlaylistshipDto, @CurrentUser() userId: number) {
	return await this.playlistshipService.deletePlaylistship(playlistshipDto, userId);
  }

  @Get('allowed-playlist-user')
  async getPlaylistUsers(@Query() id: number){
	return await this.playlistshipService.getPlaylistUsers(id);
  }
}
