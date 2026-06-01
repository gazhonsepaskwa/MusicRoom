import {
  BadRequestException,
  Controller,
  Get,
  Param,
  Query,
} from '@nestjs/common';
import { SearchService } from './search.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';

@Controller('search')
export class SearchController {
  constructor(private readonly searchService: SearchService) {}

  @Get()
  getArtist(
    @Query('query') query: string,
    @Query('type') types: string,
    @Query('offset', ParseSafeIntPipe) offset: number,
    @Query('limit', ParseSafeIntPipe) limit: number,
  ) {
    console.log(
      'SearchController.getArtist called with query:',
      query,
      'offset:',
      offset,
      'limit:',
      limit,
    );

    const typeArray = types?.split(',') || ['artist', 'music', 'album'];

    const validTypes = ['artist', 'music', 'album'];
    const invalidTypes = typeArray.filter((type) => !validTypes.includes(type));
    if (invalidTypes.length > 0) {
      throw new BadRequestException(
        `Types invalides : ${invalidTypes.join(', ')}. Types autorisés : artist, music, album.`,
      );
    }

    return this.searchService.search(query, typeArray, offset, limit);
  }
}
