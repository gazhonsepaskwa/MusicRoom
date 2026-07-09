import { forwardRef, Module } from '@nestjs/common';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { UsersModule } from '../users/users.module';
import { JwtModule } from '@nestjs/jwt';
import { jwtConstants } from './constant';
import { MailModule } from '../mail/mail.module';
import { PassportModule } from '@nestjs/passport';
import { OAuthStrategy } from './oauth.strategy';
import { AuthGuard } from './auth.guard';
import { PlaylistsModule } from '../playlists/playlists.module';

@Module({
  imports: [
    UsersModule,
    MailModule,
    PassportModule,
    JwtModule.register({
      global: true,
      secret: jwtConstants.secret,
      signOptions: { expiresIn: '31day' },
    }),
	forwardRef(() => PlaylistsModule),
  ],
  controllers: [AuthController],
  providers: [AuthGuard, AuthService, OAuthStrategy],
  exports: [AuthService, AuthGuard],
})
export class AuthModule {}
  