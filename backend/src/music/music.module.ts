import { Module } from '@nestjs/common';
import { MusicService } from './music.service';
import { MusicController } from './music.controller';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  providers: [MusicService],
  controllers: [MusicController],
  exports: [MusicService]
})
export class MusicModule {}
