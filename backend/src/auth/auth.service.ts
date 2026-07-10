import {
  BadRequestException,
  forwardRef,
  Inject,
  Injectable,
  UnauthorizedException,
  UnprocessableEntityException,
} from '@nestjs/common';
import { UsersService } from '../users/users.service';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { MailService } from '../mail/mail.service';
import { PlaylistsService } from '../playlists/playlists.service';

@Injectable()
export class AuthService {
  constructor(
    private readonly usersService: UsersService,
    private jwtService: JwtService,
    private mailService: MailService,
	@Inject(forwardRef(() => PlaylistsService))
	private playlistsService: PlaylistsService,
  ) {}

  async signIn(
    username: string,
    pass: string,
  ): Promise<{ access_token: string }> {
    const userByUsername = await this.usersService.user({ username: username });
    const userByEmail = await this.usersService.user({ email: username });
    const user = userByUsername ?? userByEmail;
    if (user == undefined || user.username == null)
      throw new UnprocessableEntityException(
        'Incorrect Password or User',
        'Invalid Log In Attempt ',
      );
	if (user.verifiedEmail == false)
		throw new UnprocessableEntityException(
			'This account does not exist or was not confirmed by email. If you have created an account, check your mailbox!'
		);
    if (user.password == undefined || user.password == null)
      throw new UnprocessableEntityException(
        'Connect with your google account and change your password!',
      );
    const hash = await bcrypt.compare(pass, user?.password);
    if (hash == false) {
      throw new UnprocessableEntityException(
        'Incorrect Password or User',
        'Invalid Log In Attempt ',
      );
    }
    const payload = { sub: user.id, username: user.username };
    return {
      access_token: await this.jwtService.signAsync(payload),
    };
  }

  async signUp(username: string, password: string, email: string) {
    const userByEmail = await this.usersService.user({ email });
    if (userByEmail && userByEmail.verifiedEmail)
      throw new UnprocessableEntityException(
        `email already used: ${email}`,
        'Invalid Account Creation',
      );
	else if (userByEmail && !userByEmail.verifiedEmail) {
		await this.usersService.deleteUser({ id: userByEmail.id });
	}
    const userByUsername = await this.usersService.user({ username });
    if (userByUsername)
      throw new UnprocessableEntityException(
        `username already used: ${username}`,
        'Invalid Account Creation',
      );
    const salt = await bcrypt.genSalt();
    const hash = await bcrypt.hash(password, salt);
    const user = await this.usersService.createUser({
      password: hash,
      username: username,
      email: email,
    });
    if (!user.email)
      throw new BadRequestException('Missing email for verification');
    this.sendVerificationEmail(user.email, user.id);
    return {
      message:
        'Please Check your mailbox for the verfication email we have send you (you have 10 minutes)',
    };
  }

  async getUserFromJWT(token: string) {
    try {
      const payload = await this.jwtService.verifyAsync(token);
      const user = await this.usersService.user({ id: payload.sub });
      return user?.id;
    } catch {
      throw new UnauthorizedException();
    }
  }

  async sendVerificationEmail(email: string, id?: Number) {
    const user = await this.usersService.user({ email });
    if (!user)
      throw new UnauthorizedException('No user at this email address!');
    if (!id) id = user.id;
    const token = this.jwtService.sign(
      {
        sub: id,
        purpose: 'verify-email',
      },
      {
        expiresIn: '10min',
      },
    );
	const domainName = process.env.DOMAIN_NAME == 'localhost' ? process.env.DOMAIN_NAME + (process.env.EXTERNAL_PORT ? `:${process.env.EXTERNAL_PORT}` : '') : process.env.DOMAIN_NAME;
	const link = `https://${domainName}/auth/verify?verificationToken=${token}`;
    this.mailService.sendVerificationEmail(email, link);
  }

  async generateJWToken(user: any): Promise<{ access_token: string }> {
    const Payload = { sub: user.id, username: user.username };
    return {
      access_token: await this.jwtService.signAsync(Payload),
    };
  }
  async confirmEmail(token: string) {
    try {
      const payload = await this.jwtService.verifyAsync(token);
	  if ((await this.usersService.user({ id: payload.sub }))?.verifiedEmail == false) {
		const user = await this.usersService.updateUser({
			where: { id: payload.sub },
			data: { verifiedEmail: true },
		});
			await this.playlistsService.create({
				isPublic: false, 
				title: "Favorite", 
				isDefault: true, 
				status: "FAVORITE",
				user: {
				connect: {
					id: +user.id,
				},
			}, 
			});
		}
    } catch {
      throw new UnauthorizedException(
        'The link has expired or was corrupted. The data you have Send have been deleted. Sign up again',
      );
    }
  }

  async loginFromVerificationToken(token: string): Promise<{ access_token: string }> {
	try {
	  const payload = await this.jwtService.verifyAsync(token);
	  const user = await this.usersService.user({ id: payload.sub });
	  if (!user || !user.verifiedEmail) {
		throw new UnauthorizedException();
	  }
	  return this.generateJWToken(user);
	} catch {
	  throw new UnauthorizedException(
		'The link has expired or was corrupted. The data you have Send have been deleted. Sign up again',
	  );
	}
  }

  async validateOAuthLogin(profile: any): Promise<any> {
    let user = await this.usersService.user({ email: profile.email });
    if (!user) {
      user = await this.usersService.createUser({
        username: profile.username,
        email: profile.email,
        verifiedEmail: true,
      });
    }
    if (!profile || !user) {
      throw new UnauthorizedException();
    }

    return this.generateJWToken(user);
  }

  async deleteUserAccount(userId: number): Promise<void> {
	await this.playlistsService.deleteAllUserPlaylists(userId);
	await this.usersService.deleteUser({ id: userId });
  }
}
