import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { UsersModule } from './users/users.module';
import { PrismaService } from './prisma/prisma.service';
import { PlaylistsModule } from './playlists/playlists.module';
import { MusicModule } from './music/music.module';
import { APP_GUARD } from '@nestjs/core/constants';
import { AuthGuard } from './auth/auth.guard';
import { MailService } from './mail/mail.service';
import { MailModule } from './mail/mail.module';
import { MusicModule } from './music/music.module';
import { FriendshipModule } from './friendship/friendship.module';
import { AlbumModule } from './album/album.module';
import { ArtistModule } from './artist/artist.module';
import { SearchModule } from './search/search.module';
import { WebsocketsModule } from './websockets/websockets.module';

@Module({
  imports: [
    AuthModule,
    UsersModule,
    MailModule,
    PlaylistsModule,
    MusicModule,
    FriendshipModule,
    AlbumModule,
    ArtistModule,
    SearchModule,
    WebsocketsModule,
  ],
  controllers: [AppController],
  providers: [
    AppService,
    PrismaService,
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
    MailService,
  ],
})
export class AppModule {}
