import { BadRequestException, Injectable, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';
import { MailService } from '../mail/mail.service';

@Injectable()
export class AuthService {
	constructor(
			private readonly usersService: UsersService,
			private jwtService: JwtService,
			private mailService: MailService,
		) {}

	async signIn(username: string, pass: string): Promise<{access_token: string}> {
		const user = await this.usersService.user({username});
		if (user == undefined)
			throw new UnprocessableEntityException("Incorrect Password or Username", "Invalid Loging Attempt ");
		if (user.password == undefined || user.password == null)
			throw new UnprocessableEntityException("Connect with your google account and change your password!");
		const hash = await bcrypt.compare(pass, user?.password);
		if (hash == false) {
			throw new UnprocessableEntityException("Incorrect Password or Username", "Invalid Loging Attempt ");
		}
		const payload = { sub: user.id, username: user.username };
		return {
			access_token: await this.jwtService.signAsync(payload),
		};
	}

	async signUp(username: string, password: string, email: string) {
		const userByEmail = await this.usersService.user({email});
		if (userByEmail)
			throw new UnprocessableEntityException(`email already used: ${email}`, "Invalid Account Creation"); // retourner un message disant qu'un compte existe deja à cette adresse
		const userByUsername = await this.usersService.user({username});
		if (userByUsername)
			throw new UnprocessableEntityException(`username already used: ${username}`, "Invalid Account Creation"); // retourner un message disant qu'un compte existe deja à cette username
		const salt = await bcrypt.genSalt();
		const hash = await bcrypt.hash(password, salt);
		const user = await this.usersService.createUser({
			password: hash,
			username: username,
			email: email,
		});
		if (!user.email)
			throw new BadRequestException("Missing email for verification");
		this.sendVerificationEmail(user.email, user.id);
		return {message: "Please Check your mailbox for the verfication email we have send you (you have 1 hour)"};
	}

	async getUserFromJWT(token: string) {
		try {
			const payload = await this.jwtService.verifyAsync(token);
			const user = await this.usersService.user({id: payload.sub});
			return user?.id;
		}
		catch {
			throw new UnauthorizedException();
		}
	}

	async sendVerificationEmail(email: string, id?: Number) {
		console.log("Sending verification email to", email);
		const user = await this.usersService.user({email});
		if (!user)
			throw new UnauthorizedException("No user at this email address!")
		if (!id)
			id = user.id;
		const token = this.jwtService.sign(
			{
				sub: id,
				purpose: 'verify-email',
			},
			{
				expiresIn: '1h',
			},
		);
		const link = `https://${process.env.DOMAIN_NAME}/auth/verify?verificationToken=${token}`;
		this.mailService.sendVerificationEmail(email, link);
	}

	async generateJWToken(user: any) : Promise<{access_token: string}> {
		const Payload = { sub: user.id, username: user.username };
		return {
			access_token: await this.jwtService.signAsync(Payload),
		};
	}
	async confirmEmail(token: string) :  Promise<{access_token: string}> {
		try {
			const payload = await this.jwtService.verifyAsync(token);
			const user = await this.usersService.updateUser({
				where: { id: payload.sub },
				data: { verifiedEmail: true },
			});
			return this.generateJWToken(user);
		}
		catch {
			throw new UnauthorizedException("The link has expired or was corrupted. The data you have Send have been deleted. Sign up again");
		}
	}

	async validateOAuthLogin(profile: any): Promise<any> {
		// Here you should validate the user (e.g., check if the user exists in the database)
		// and create a new user if it doesn't. This implementation is for demonstration purposes.
		
		let user = await this.usersService.user({email: profile.email})
		if (!user) {
			user = await this.usersService.createUser({
				username: profile.username,
				email: profile.email,
				verifiedEmail: true,
			})
		}
		if (!profile || !user) {
			throw new UnauthorizedException();
		}

		return this.generateJWToken(user);
  }
}

