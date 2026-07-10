import {
  Controller,
  Get,
  Post,
  Param,
  Body,
  Query,
  BadRequestException,
  Patch,
  UnauthorizedException,
} from '@nestjs/common';
import { UsersService } from './users.service';
import { ApiOkResponse, ApiQuery } from '@nestjs/swagger';
import { UserProfileResponseDto, UserResponseDto, UserUpdateDto } from './dto/user.dto';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { ParseSafeIntPipe } from '../common/pipe/parse_safe_int.pipe';
import { profile } from 'console';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  //a garder ou pas ?
  @ApiQuery({ name: 'id', required: false, type: String })
  @ApiQuery({ name: 'username', required: false, type: String })
  @ApiOkResponse({ type: UserResponseDto })
  @Get()
  async getUser(
    @Query('id') id?: string,
    @Query('username') username?: string,
  ) {
    let user: any = null;
    if (id) {
      user = await this.usersService.user({ id: +id });
    }

    if (username) {
      user = await this.usersService.user({ username });
    }
    if (user == null) throw new BadRequestException();
    return {
      id: user.id,
      username: user.username,
      email: user.email,
    };
  }

  @ApiOkResponse({type: UserProfileResponseDto})
  @Get('profile/:id')
  async getProfile(@CurrentUser() userId: number, @Param('id', ParseSafeIntPipe) profileId: number) {
	return await this.usersService.getUserProfile(profileId, userId);
  }

  @Patch('update/:id')
  async updateProfile(
	@CurrentUser() userId: number,
	@Param('id', ParseSafeIntPipe) profileId: number,
	@Body() data: UserUpdateDto)
  {
	if (userId != profileId)
		throw new UnauthorizedException("You do not own this profile")
	if (data.password)
		data.password = await this.usersService.encryptPassword(data.password);
	await this.usersService.updateUser({ where:{id: profileId}, data})
  }
}
