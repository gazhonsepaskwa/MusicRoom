import { Controller, Get, Param, ParseIntPipe } from '@nestjs/common';
import { ArtistService } from '../artist/artist.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';

@Controller('artist')
export class ArtistController {
  constructor(private readonly artistService: ArtistService) {}

  @Get(':id')
  getArtist(@Param('id', ParseSafeIntPipe) id: number) {
    return this.artistService.artist({
      id,
    });
  }
}
