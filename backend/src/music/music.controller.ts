import {
  Controller,
  Get,
  Header,
  NotFoundException,
  Param,
  Res,
} from '@nestjs/common';
import { MusicService } from './music.service';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import * as fs from 'fs';
import { Response } from 'express';
import { ApiOkResponse, ApiParam } from '@nestjs/swagger';
import { MusicResponseDto } from './dto/music.dto';

@Controller('music')
export class MusicController {
  constructor(private readonly musicService: MusicService) {}

  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ type: MusicResponseDto })
  @Get(':id')
  async getMusic(@Param('id', ParseSafeIntPipe) id: number) {
    return await this.musicService.music({
      id,
    });
  }

  @Get('stream/:id')
  @Header('Accept-Ranges', 'bytes')
  async streamMusic(
    @Param('id', ParseSafeIntPipe) id: number,
    @Res() res: Response,
  ) {
    try {
      const filePath = await this.musicService.streamMusic({ id });
      const fileStream = fs.createReadStream(filePath);
      fileStream.on('error', (error: NodeJS.ErrnoException) => {
        if (error.code === 'ENOENT') {
          res.status(404).send('Music file not found');
        } else {
          res.status(400).send('Internal server error');
        }
      });
      res.setHeader('Content-Type', 'audio/mpeg');
      fileStream.pipe(res);
    } catch (error) {
      if (
        error instanceof Error &&
        'code' in error &&
        error.code === 'ENOENT'
      ) {
        throw new NotFoundException('Music file not found');
      }

      console.error('Error occurred while streaming music:', error);
      res.status(404).send('Music not found');
    }
  }
}
