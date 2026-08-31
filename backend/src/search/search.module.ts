import { Module } from '@nestjs/common';
import { SearchService } from './search.service';
import { SearchController } from './search.controller';
import { AuthModule } from '../auth/auth.module';
import { UsersModule } from '../users/users.module';
import { PlaylistsModule } from '../playlists/playlists.module';
import { AlbumModule } from '../album/album.module';
import { ArtistModule } from '../artist/artist.module';
import { MusicModule } from '../music/music.module';
import { PrismaModule } from '../prisma/prisma.module';
import { DevicesModule } from '../devices/devices.module';
import { WebsocketsModule } from '../websockets/websockets.module';

@Module({
  imports: [AuthModule, 
	MusicModule, 
	ArtistModule, 
	AlbumModule, 
	PlaylistsModule, 
	UsersModule,
	PrismaModule,
	WebsocketsModule,
	DevicesModule
],
  controllers: [SearchController],
  providers: [
    SearchService,
  ],
  exports: [SearchService]
})
export class SearchModule {}
