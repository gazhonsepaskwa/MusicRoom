import { Controller, Get, Post, Param, Body, Query, BadRequestException } from '@nestjs/common';
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

	//rajouter confirmation par email
	//checker que le mot de passe est assez fort
	//verifier que le username et email sont conformes aux regex
	@Post()
	createUser(@Body() body: {
			password: string;
			name: string;
			email: string;},)
	{
		this.usersService.createUser({
			password: body.password,
			username: body.name,
			email: body.email,
		});
	}
}
