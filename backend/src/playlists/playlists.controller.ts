import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  NotFoundException,
  Param,
  Patch,
  Post,
  Query,
  Req,
} from '@nestjs/common';
import { PlaylistsService } from './playlists.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import { AuthGuard } from '../auth/auth.guard';
import { Request } from 'express';

@Controller('playlists')
export class PlaylistsController {
  constructor(
    private readonly playlistsService: PlaylistsService,
    private readonly authGuard: AuthGuard,
  ) {}

  @Post('create')
  create(
    @Body()
    body: {
      title: string;
      status: string;
      isPublic: boolean;
    },
    @Req() req: Request,
  ) {
    const userId = this.authGuard.getUserIdFromRequest(req);
    if (!userId) {
      throw new BadRequestException('User ID not found in request');
    }
    try {
      return this.playlistsService.create({
        ...body,
        user: { connect: { id: userId } },
      });
    } catch (error) {
      throw new BadRequestException(error);
    }
  }

  @Patch('update/:id')
  updatePublicStatus(
    @Param('id', ParseSafeIntPipe) id: number,
    @Body() body: { title?: string; status?: string; isPublic?: boolean },
  ) {
    return this.playlistsService.update({
      where: { id },
      data: body,
    });
  }

  @Get('get/:id')
  get(@Param('id', ParseSafeIntPipe) id: number) {
    return this.playlistsService.playlist({ id });
  }

  @Delete('delete/:id')
  delete(@Param('id', ParseSafeIntPipe) id: number) {
    return this.playlistsService.delete({ id });
  }
}
