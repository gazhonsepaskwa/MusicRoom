import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy } from 'passport-oauth2';
import { AuthService } from './auth.service';
import * as fs from 'fs';

@Injectable()
export class OAuthStrategy extends PassportStrategy(Strategy, 'oauth') {
  constructor(private readonly authService: AuthService) {
	const googlefile = fs.readFileSync("/run/secrets/google_credentials", "utf8");
	const googleCredentials = JSON.parse(googlefile); 
    super({
      authorizationURL: googleCredentials.auth_uri,
      tokenURL: googleCredentials.token_uri,
      clientID: googleCredentials.client_id,
      clientSecret: googleCredentials.private_key,
      callbackURL: process.env.REDIRECT_TO_LOGIN,
    });
  }

  async validate(accessToken: string, refreshToken: string, profile: any): Promise<any> {
    const user = await this.authService.validateOAuthLogin(profile);
    return user;
  }
}