import { Body, Controller, Get, Post, Query } from '@nestjs/common';
import { PlaylistshipService } from './playlistship.service';
import { PlaylistshipDto } from './dto/playlistship.dto';

@Controller('playlistship')
export class PlaylistshipController {
  constructor(private readonly playlistshipService: PlaylistshipService) {}

  @Post('send-playlist-invitation')
  async sendInvitation(@Body() playlistshipDto: PlaylistshipDto) {
	return await this.playlistshipService.sendPlaylistInvitation(
		playlistshipDto.playlistId, 
		playlistshipDto.addresseeId
	);
  }

  @Post('answer-playlist-invitation')
  async answerInvitation(@Body() playlistshipDto: PlaylistshipDto){
	return await this.playlistshipService.answerPlaylistInvitation(playlistshipDto);
  }

  @Post('leave-playlist')
  async leavePlaylist(@Body() playlistshipDto: PlaylistshipDto) {
	const {playlistId, addresseeId} = playlistshipDto;
	return await this.playlistshipService.deletePlaylistship(playlistId, addresseeId);
  }

  @Get('allowed-playlist-user')
  async getPlaylistUsers(@Query() id: number){
	return await this.playlistshipService.getPlaylistUsers(id);
  }
}
