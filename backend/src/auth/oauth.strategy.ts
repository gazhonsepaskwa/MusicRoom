import { Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { Strategy } from 'passport-oauth2';
import { AuthService } from './auth.service';
import * as fs from 'fs';

@Injectable()
export class OAuthStrategy extends PassportStrategy(Strategy, 'oauth') {
  constructor(private readonly authService: AuthService) {
	const domainName = process.env.DOMAIN_NAME == 'localhost' ? process.env.DOMAIN_NAME + (process.env.EXTERNAL_PORT ? `:${process.env.EXTERNAL_PORT}` : '') : process.env.DOMAIN_NAME;
	const redirectToLogin = `https://${domainName}/auth/oauth/callback`; // This needs to be checked if working as the callback address in google account is set to localhost
	const googlefile = fs.readFileSync("/run/secrets/oauth_client_id_wa", "utf8");
	const googleCredentials = JSON.parse(googlefile); 
    super({
      authorizationURL: googleCredentials.web.auth_uri,
      tokenURL: googleCredentials.web.token_uri,
      clientID: googleCredentials.web.client_id,
      clientSecret: googleCredentials.client_secret,
      callbackURL: redirectToLogin,
	  scope: ['profile', 'email'],
    });
  }

  async validate(accessToken: string, refreshToken: string, profile: any): Promise<any> {
    const user = await this.authService.validateOAuthLogin(profile);
    return user;
  }
}