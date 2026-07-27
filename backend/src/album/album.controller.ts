import { Controller, Get, Param } from '@nestjs/common';
import { ApiOkResponse, ApiParam } from '@nestjs/swagger';
import { AlbumService } from './album.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import { AlbumResponseDto } from './dto/album.dto';

@Controller('album')
export class AlbumController {
  constructor(private readonly albumService: AlbumService) {}

  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ type: AlbumResponseDto })
  @Get(':id')
  getAlbum(@Param('id', ParseSafeIntPipe) id: number) {
    return this.albumService.album({
      id,
    });
  }
}
