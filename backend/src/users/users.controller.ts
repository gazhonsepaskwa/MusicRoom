import { Controller, Get, Post, Param, Body } from '@nestjs/common';
import { UsersService } from './users.service';

@Controller('users')
export class UsersController {
	constructor(private readonly usersService: UsersService) {}

	@Get(':id')
	getUser(@Param('id') id: number,) {
		return this.usersService.user({
			id: id,
		});
	}

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
