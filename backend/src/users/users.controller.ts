import { Controller, Get, Post, Param, Body, Query, BadRequestException, Headers } from '@nestjs/common';
import { UsersService } from './users.service';
import { PreferredArtistDto } from './dto/preferredArtist.dto';
import { AuthService } from '../auth/auth.service';
import { request } from 'express';

@Controller('users')
export class UsersController {
	constructor(private readonly usersService: UsersService, private readonly authService: AuthService) {}

	//a garder ou pas ?
	@Get()
	async getUser(
		@Query('id') id?: string,
		@Query('username') username?: string,
	) {
		let user : any = null;
		if (id) {
			user = await this.usersService.user({ id: +id });
		}

		if (username) {
			user = await this.usersService.user({ username });
		}
		if (user == null)
			throw new BadRequestException();
		return {
			id: user.id,
			username: user.username,
			email: user.email,
		};
	}
	@Post('preferred-artist')
	async setPreferredArtist(@Body() preferredArtistDto: PreferredArtistDto, @Headers('Authorization') authHeader: any) {
		const { index, artistId } = preferredArtistDto;
		if (index < 1 || index > 3) {
			throw new BadRequestException('Index must be between 1 and 3');
		}
		const userId = await this.authService.getUserFromJWT(authHeader);
		return this.usersService.setPreferredArtist(index, userId, artistId);
	}
}
