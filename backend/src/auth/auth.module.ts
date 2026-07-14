import { Module } from '@nestjs/common';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { UsersModule } from '../users/users.module';
import { JwtModule } from '@nestjs/jwt';
import { jwtConstants } from './constant';
import { MailModule } from '../mail/mail.module';
import { PassportModule } from '@nestjs/passport';
import { OAuthStrategy } from './oauth.strategy';
import { DevicesModule } from '../devices/devices.module';
import { PrismaService } from '../prisma/prisma.service';
import { WebsocketsModule } from '../websockets/websockets.module';
import { forwardRef } from '@nestjs/common';
import { DevicesService } from '../devices/devices.service';
import { WebSocketsService } from '../websockets/websockets.service';

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
    forwardRef(() => DevicesModule),
    forwardRef(() => WebsocketsModule),
  ],
  controllers: [AuthController],
  providers: [AuthService, OAuthStrategy, PrismaService],
  exports: [AuthService],
})
export class AuthModule {}
