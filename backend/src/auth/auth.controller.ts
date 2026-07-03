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
import { EmailDto } from './dto/email.dto';
import { SignInDto } from './dto/signIn.dto';
import { ApiBody, ApiOkResponse, ApiQuery } from '@nestjs/swagger';
import { AuthMessageResponseDto, AuthTokenResponseDto, UserProfileResponseDto } from './dto/auth-response.dto';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

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

  @ApiOkResponse({ type: UserProfileResponseDto })
  @Get('profile')
  getProfile(@Request() req) {
    return req.user;
  }

  @ApiQuery({ name: 'verificationToken', required: false })
  @ApiOkResponse({ type: AuthTokenResponseDto })
  @Get('verify')
  @Public()
  verifyEmail(@Query('verificationToken') token?: string) {
    if (token) return this.authService.confirmEmail(token);
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
}
