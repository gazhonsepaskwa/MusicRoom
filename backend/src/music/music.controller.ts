import { Controller, Get, Param } from '@nestjs/common';
import { MusicService } from './music.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';

@Controller('music')
export class MusicController {
  constructor(private readonly musicService: MusicService) {}

  @Get(':id')
  getMusic(@Param('id', ParseSafeIntPipe) id: number) {
    console.log('MusicController.getMusic called with id:', id);
    return this.musicService.music({
      id,
    });
  }
}
