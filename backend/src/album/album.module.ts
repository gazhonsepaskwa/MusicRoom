import { Module } from '@nestjs/common';
import { AlbumController } from './album.controller';
import { AlbumService } from './album.service';
import { PrismaService } from '../prisma/prisma.service';

@Module({
  controllers: [AlbumController],
  providers: [AlbumService, PrismaService],
})
export class AlbumModule {}
