import {
  Controller,
  Get,
  Post,
  Param,
  Body,
  Query,
  BadRequestException,
} from '@nestjs/common';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  //a garder ou pas ?
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
}
