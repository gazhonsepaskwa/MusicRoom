import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { UsersModule } from './users/users.module';
import { PrismaService } from './prisma/prisma.service';
import { PlaylistsModule } from './playlists/playlists.module';
import { MusicModule } from './music/music.module';

@Module({
  imports: [AuthModule, UsersModule, PlaylistsModule, MusicModule],
  controllers: [AppController],
  providers: [AppService, PrismaService],
})
export class AppModule {}
