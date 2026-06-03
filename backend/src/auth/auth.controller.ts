import { Body, Controller, Post, HttpCode, HttpStatus, Get, UseGuards, Request, Query } from '@nestjs/common';
import { AuthService } from './auth.service';
import { Public } from './auth.guard';

@Controller('auth')
export class AuthController {
	constructor(private readonly authService: AuthService) {}

	@HttpCode(HttpStatus.OK)
	@Public()
	@Post('login')
	signIn(@Body() signInDto: Record<string, any>) {
		return this.authService.signIn(signInDto.username, signInDto.password);
	}

	//USE DTO CLASS INSTEAD OF RECORD<>
	@HttpCode(HttpStatus.OK)
	@Public()
	@Post('new_account')
	signUp(@Body() signUpDto: Record<string, any>) {
		console.log(process.env.SMTP_HOST);
		return this.authService.signUp(signUpDto.username, signUpDto.password, signUpDto.email);
	}

	@Get('profile')
	getProfile(@Request() req) {
		return req.user;
	}

	@Get('verify')
	@Public()
	verifyEmail(@Query('verificationToken') token?: string) {
		if (token)
			return this.authService.confirmEmail(token);
	}

	@Post('resend-email')
	@Public()
	resendVerificationEmail(@Body('email') email: string){
		this.authService.sendVerificationEmail(email);
	}
}