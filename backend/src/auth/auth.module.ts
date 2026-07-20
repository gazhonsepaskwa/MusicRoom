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
import { DevicesModule } from '../devices/devices.module';
import { WebsocketsModule } from '../websockets/websockets.module';



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
	PlaylistsModule,
    forwardRef(() => DevicesModule),
    forwardRef(() => WebsocketsModule),
  ],
  controllers: [AuthController],
  providers: [AuthGuard, AuthService, OAuthStrategy],
  exports: [AuthService, AuthGuard],
})
export class AuthModule {}
  