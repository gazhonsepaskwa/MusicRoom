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
} from '@nestjs/common';
import { AuthService } from './auth.service';
import { Public } from './auth.guard';
import { AuthGuard } from '@nestjs/passport';
import { NewUserDto } from './dto/newUser.dto';
import { SignInDto } from './dto/signIn.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @HttpCode(HttpStatus.OK)
  @Public()
  @Post('login')
  signIn(@Body() signInDto: SignInDto) {
    return this.authService.signIn(signInDto.username, signInDto.password);
  }

  @HttpCode(HttpStatus.OK)
  @Public()
  @Post('new_account')
  signUp(@Body() newUserDto: NewUserDto) {
    const { username, password, email } = newUserDto;
    return this.authService.signUp(username, password, email);
  }

  @Get('profile')
  getProfile(@Request() req) {
    return req.user;
  }

  @Get('verify')
  @Public()
  verifyEmail(@Query('verificationToken') token?: string) {
    if (token) return this.authService.confirmEmail(token);
  }

  @Post('resend-email')
  @Public()
  resendVerificationEmail(@Body('email') email: string) {
    this.authService.sendVerificationEmail(email);
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
}
