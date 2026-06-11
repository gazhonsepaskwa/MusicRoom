import { Controller, Get, Param } from '@nestjs/common';
import { AlbumService } from './album.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';

@Controller('album')
export class AlbumController {
  constructor(private readonly albumService: AlbumService) {}

  @Get(':id')
  getAlbum(@Param('id', ParseSafeIntPipe) id: number) {
    console.log('AlbumController.getAlbum called with id:', id);
    return this.albumService.album({
      id,
    });
  }
}
