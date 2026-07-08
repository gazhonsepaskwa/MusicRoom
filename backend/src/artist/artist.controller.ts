import { Controller, Get, Param } from '@nestjs/common';
import { ApiOkResponse, ApiParam } from '@nestjs/swagger';
import { ArtistService } from '../artist/artist.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import { ArtistResponseDto } from './dto/artist.dto';

@Controller('artist')
export class ArtistController {
  constructor(private readonly artistService: ArtistService) {}

  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ type: ArtistResponseDto })
  @Get(':id')
  getArtist(@Param('id', ParseSafeIntPipe) id: number) {
    return this.artistService.artist({
      id,
    });
  }
}
