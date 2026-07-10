import { Module } from '@nestjs/common';
import { BaseGateway } from './base.gateway';
import { WebSocketsService } from './websockets.service';
import { JwtModule } from '@nestjs/jwt';
import { AuthModule } from '../auth/auth.module';
import { jwtConstants } from '../auth/constant';
import { PrismaModule } from '../prisma/prisma.module';


@Module({
  imports: [JwtModule.register({
		global: true,
		secret: jwtConstants.secret,
		signOptions: { expiresIn: '31day' },
	  }),
	  PrismaModule
	],
  providers: [BaseGateway, WebSocketsService],
  exports: [BaseGateway, WebSocketsService],
})
export class WebsocketsModule {}
