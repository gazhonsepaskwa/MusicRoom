import {
  Injectable,
  UnauthorizedException,
  UnprocessableEntityException,
} from '@nestjs/common';
import { UsersService } from '../users/users.service';
import * as bcrypt from 'bcrypt';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class AuthService {
  constructor(
    private readonly usersService: UsersService,
    private jwtService: JwtService,
  ) {}

  async signIn(
    username: string,
    pass: string,
  ): Promise<{ access_token: string }> {
    const user = await this.usersService.user({ username });
    if (user == undefined)
      throw new UnprocessableEntityException(
        'Incorrect Password or Username',
        'Invalid Loging Attempt ',
      );
    const hash = await bcrypt.compare(pass, user?.password);
    if (hash == false) {
      throw new UnprocessableEntityException(
        'Incorrect Password or Username',
        'Invalid Loging Attempt ',
      );
    }
    const payload = { sub: user.id, username: user.username };
    return {
      access_token: await this.jwtService.signAsync(payload),
    };
  }

  async signUp(
    username: string,
    password: string,
    email: string,
  ): Promise<{ access_token: string }> {
    const userByEmail = await this.usersService.user({ email });
    if (userByEmail)
      throw new UnprocessableEntityException(
        `email already used: ${email}`,
        'Invalid Account Creation',
      ); // retourner un message disant qu'un compte existe deja à cette adresse
    const userByUsername = await this.usersService.user({ username });
    if (userByUsername)
      throw new UnprocessableEntityException(
        `username already used: ${username}`,
        'Invalid Account Creation',
      ); // retourner un message disant qu'un compte existe deja à cette username
    const salt = await bcrypt.genSalt();
    const hash = await bcrypt.hash(password, salt);
    const user = await this.usersService.createUser({
      password: hash,
      username: username,
      email: email,
    });
    const payload = { sub: user.id, username: user.username };
    return {
      access_token: await this.jwtService.signAsync(payload),
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
}
