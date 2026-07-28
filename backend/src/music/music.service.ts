import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '../../generated/prisma/client';
import { music } from '../../generated/prisma/browser';
import { PrismaService } from '../prisma/prisma.service';
import { MusicDto } from './dto/music.dto';

@Injectable()
export class MusicService {
  constructor(private prisma: PrismaService) {}

  async music(
    musicWhereUniqueInput: Prisma.musicWhereUniqueInput,
  ): Promise<any> {
    try {
      const result = await this.prisma.music.findUnique({
        where: musicWhereUniqueInput,
        select: {
          id: true,
          title: true,
          duration: true,
          album: {
            select: {
              title: true,
              date: true,
              images: true,
            },
          },
          artists: {
            select: {
              title: true,
            },
          },
        },
      });
      if (!result) {
        throw new NotFoundException('Music not found');
      }
      result['type'] = 'music';

      return result;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid music ID');
      }
      throw error;
    }
  }

  getMusicPath({ id }: { id: number }): string {
    return `/dl/${id}.mp3`;
  }

  async convertMusicListIdsToMusicList(
    musicListIds: number[],
  ): Promise<MusicDto[]> {
    const musicList = await this.prisma.music.findMany({
      where: {
        id: {
          in: musicListIds,
        },
      },
      select: {
        id: true,
        title: true,
        duration: true,
        album: {
          select: {
            title: true,
            date: true,
            images: true,
          },
        },
        artists: {
          select: {
            title: true,
          },
        },
      },
    });
    return musicList.map((music) => ({
      ...music,
      album: {
        ...music.album,
        date: music.album.date.toISOString(),
      },
    }));
  }

  async streamMusic(
    musicWhereUniqueInput: Prisma.musicWhereUniqueInput,
  ): Promise<string> {
    const music = await this.prisma.music.findUnique({
      where: musicWhereUniqueInput,
      select: { id: true },
    });
    if (!music) {
      throw new NotFoundException('Music not found');
    }
    return this.getMusicPath({ id: music.id });
  }
}
