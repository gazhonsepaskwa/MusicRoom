import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { UsersModule } from './users/users.module';
import { PrismaService } from './prisma/prisma.service';
import { PlaylistsModule } from './playlists/playlists.module';
import { MusicModule } from './music/music.module';
import { FriendshipModule } from './friendship/friendship.module';
import { AlbumModule } from './album/album.module';
import { ArtistModule } from './artist/artist.module';
import { SearchModule } from './search/search.module';
import { APP_GUARD } from '@nestjs/core/constants';
import { AuthGuard } from './auth/auth.guard';

@Module({
  imports: [
    AuthModule,
    UsersModule,
    PlaylistsModule,
    MusicModule,
    FriendshipModule,
    AlbumModule,
    ArtistModule,
    SearchModule,
  ],
  controllers: [AppController],
  providers: [
    AppService,
    PrismaService,
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
  ],
})
export class AppModule {}
