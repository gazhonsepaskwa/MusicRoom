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
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { MusicPlaylistDto, PlaylistListItemDto, PlaylistVersionResponseDto } from './dto/playlists.dto';
import { ApiBody, ApiOkResponse, ApiParam } from '@nestjs/swagger';
import {
  CreatePlaylistDto,
  PlaylistDetailResponseDto,
  PlaylistResponseDto,
  UpdatePlaylistDto,
} from './dto/playlists.dto';

@Controller('playlists')
export class PlaylistsController {
  constructor(
    private readonly playlistsService: PlaylistsService,
  ) {}

  @ApiBody({ type: CreatePlaylistDto })
  @ApiOkResponse({ type: PlaylistResponseDto })
  @Post('create')
  create(
    @Body() body: CreatePlaylistDto,
    @CurrentUser() userId: number,
  ) {
    try {
      return this.playlistsService.create({
        ...body,
        user: { connect: { id: userId } },
      });
    } catch (error) {
      throw new BadRequestException(error);
    }
  }

  @ApiParam({ name: 'id', type: Number })
  @ApiBody({ type: UpdatePlaylistDto })
  @ApiOkResponse({ type: PlaylistResponseDto })
  @Patch('update/:id')
  async updatePublicStatus(
	@CurrentUser() userId: number,
    @Param('id', ParseSafeIntPipe) id: number,
    @Body() body: UpdatePlaylistDto,
  ) {
	if (await this.playlistsService.canAccess(id, userId) === false) {
		throw new BadRequestException('You are not the owner of this playlist');
	}
    return await this.playlistsService.update({
      where: { id },
      data: body,
    });
  }

  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ type: PlaylistDetailResponseDto })
  @Get('get/:id')
  get(@Param('id', ParseSafeIntPipe) id: number) {
    return this.playlistsService.playlist({ id });
  }

  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ type: PlaylistResponseDto })
  @Delete('delete/:id')
  async delete(@CurrentUser() userId: number, @Param('id', ParseSafeIntPipe) id: number) {
    return await this.playlistsService.delete({ id }, userId);
  }

  @ApiOkResponse({ type: [PlaylistListItemDto] })
  @Get('available')
  async getAvailable(@CurrentUser() userId: number) {
	return await this.playlistsService.getPersonnal(userId);
  }

  @ApiOkResponse({ type: PlaylistVersionResponseDto })
  @Post('add-music')
  async addMusic(@CurrentUser() userId: number, @Body() musicPlaylistDto: MusicPlaylistDto) {
	if (await this.playlistsService.canAccess(musicPlaylistDto.playlistId, userId) === false) {
		throw new BadRequestException('You are not the owner of this playlist');
	}
	return await this.playlistsService.addMusic(musicPlaylistDto.playlistId, musicPlaylistDto.musicId);
  }

  @ApiOkResponse({ type: PlaylistVersionResponseDto })  
  @Delete('remove-music')
  async removeMusic(@CurrentUser() userId: number, @Body() musicPlaylistDto: MusicPlaylistDto) {
	if (await this.playlistsService.canAccess(musicPlaylistDto.playlistId, userId) === false) {
		throw new BadRequestException('You are not the owner of this playlist');
	}
	return await this.playlistsService.removeMusic(musicPlaylistDto.playlistId, musicPlaylistDto.musicId);
  }

  @ApiOkResponse({ type: PlaylistDetailResponseDto })
  @Get('favorite')
  async getFavorite(@CurrentUser() userId: number) {
	const favoritePlaylist = await this.playlistsService.playlist({
	  title_userId_isDefault: {
		title: 'Favorite',
		userId: userId,
		isDefault: true,
	  },
	});
	return favoritePlaylist;
  }
}
