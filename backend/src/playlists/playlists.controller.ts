import { Controller, Get, Param, UseGuards } from '@nestjs/common';
import { PlaylistsService } from './playlists.service';

// @UseGuards(JwtAuthGuard)
@Controller('playlists')
export class PlaylistsController {
  constructor(private readonly playlistsService: PlaylistsService) {}

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.playlistsService.findOne(id);
  }
}
