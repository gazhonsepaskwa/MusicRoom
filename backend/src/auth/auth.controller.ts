import {
  Body,
  Controller,
  Post,
  HttpCode,
  HttpStatus,
  Get,
  UseGuards,
  Request,
  Query,
  Req,
  Res,
  UnauthorizedException,
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { Public } from './auth.guard';
import { AuthGuard } from '@nestjs/passport';
import { DeleteAccountDto, NewUserDto } from './dto/newUser.dto';
import { EmailDto } from './dto/email.dto';
import { SignInDto } from './dto/signIn.dto';
import { ApiBody, ApiOkResponse, ApiQuery } from '@nestjs/swagger';
import { AuthMessageResponseDto, AuthTokenResponseDto, AuthProfileResponseDto } from './dto/auth-response.dto';
import { Response } from 'express';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { UsersService } from '../users/users.service';
import { UserResponseDto } from '../users/dto/user.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService,
	private usersService: UsersService,
  ) {}

  @ApiBody({ type: SignInDto })
  @ApiOkResponse({ type: AuthTokenResponseDto })
  @HttpCode(HttpStatus.OK)
  @Public()
  @Post('login')
  signIn(@Body() signInDto: SignInDto) {
    return this.authService.signIn(signInDto.username, signInDto.password);
  }

  @ApiBody({ type: NewUserDto })
  @ApiOkResponse({ type: AuthMessageResponseDto })
  @HttpCode(HttpStatus.OK)
  @Public()
  @Post('new_account')
  signUp(@Body() newUserDto: NewUserDto) {
    const { username, password, email } = newUserDto;
    return this.authService.signUp(username, password, email);
  }

  @ApiOkResponse({ type: AuthProfileResponseDto })
  @Get('profile')
  getProfile(@Request() req) {
    return req.user;
  }

  @ApiQuery({ name: 'verificationToken', required: false })
  @Get('verify')
  @Public()
  async verifyEmail(/*@Res() res: Response,*/ @Query('verificationToken') token?: string) {
    if (!token) {
		throw new UnauthorizedException(
			'Missing verification token.',
		);
	}
	await this.authService.confirmEmail(token);
	const jwt = await this.authService.loginFromVerificationToken(token);
	return jwt;
	// return res.redirect(`${process.env.APP_SCHEME}://auth/callback?verificationToken=` + token);
  }

  @Post('callback')
  @Public()
  async callback(@Query('verificationToken') token?: string) {
	if (!token) {
		throw new UnauthorizedException(
			'Missing verification token.',
		);
	}
	return await this.authService.loginFromVerificationToken(token);
  }

  @ApiBody({ type: EmailDto })
  @ApiOkResponse({ type: AuthMessageResponseDto })
  @Post('resend-email')
  @Public()
  resendVerificationEmail(@Body() emailDto: EmailDto) {
    this.authService.sendVerificationEmail(emailDto.email);
  }

  @Get('oauth')
  @Public()
  @UseGuards(AuthGuard('oauth'))
  async oauthLogin(@Req() req) {}

  @Get('oauth/callback')
  @UseGuards(AuthGuard('oauth'))
  async oauthCallback(@Req() req) {
    const user = req.user;
    return this.authService.validateOAuthLogin(user);
  }

  @ApiOkResponse({type: UserResponseDto})
  @Post('delete-account')
  async deleteAccount(@CurrentUser() userId: number, @Body() data: DeleteAccountDto) {
	const user = (await this.usersService.user({id: userId}))!
	await this.authService.confirmPassword(user, data.password);
	return await this.usersService.deleteUser({id: userId});
  }
}
