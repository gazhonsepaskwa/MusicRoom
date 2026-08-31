import {
  BadRequestException,
  Controller,
  Get,
  Param,
  Query,
  Req,
} from '@nestjs/common';
import { SearchService } from './search.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import { AuthGuard } from '../auth/auth.guard';
import { Request } from 'express';
import { ApiOkResponse, ApiQuery } from '@nestjs/swagger';
import { SearchQueryDto } from './dto/search.dto';

@Controller('search')
export class SearchController {
  constructor(
    private readonly searchService: SearchService,
    private readonly authGuard: AuthGuard,
  ) {}

  @ApiQuery({ name: 'query', required: true })
  @ApiQuery({ name: 'type', required: false })
  @ApiQuery({ name: 'offset', required: false, type: Number })
  @ApiQuery({ name: 'limit', required: false, type: Number })
  @ApiOkResponse({ type: [Object] })
  @Get()
  deep_search(
    @Req() req: Request,
    @Query('query') query: string,
    @Query('type') types: string,
    @Query('offset', ParseSafeIntPipe) offset: number,
    @Query('limit', ParseSafeIntPipe) limit: number,
  ) {

    const typeArray = types?.split(',') || [
      'artist',
      'music',
      'album',
      'playlist',
      'user',
    ];

    const userId = this.authGuard.getUserIdFromRequest(req);
    if (!userId) {
      throw new BadRequestException('User ID not found in request');
    }
    const validTypes = ['artist', 'music', 'album', 'playlist', 'user'];
    const invalidTypes = typeArray.filter((type) => !validTypes.includes(type));
    if (invalidTypes.length > 0) {
      throw new BadRequestException(
        `Types invalides : ${invalidTypes.join(', ')}. Types autorisés : artist, music, album, playlist, user.`,
      );
    }

    return this.searchService.search(query, userId, typeArray, offset, limit);
  }
}
