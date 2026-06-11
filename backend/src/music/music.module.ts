import { Module } from '@nestjs/common';
import { MusicService } from './music.service';
import { MusicController } from './music.controller';
import { PrismaService } from '../prisma/prisma.service';

@Module({
  providers: [MusicService, PrismaService],
  controllers: [MusicController],
})
export class MusicModule {}
