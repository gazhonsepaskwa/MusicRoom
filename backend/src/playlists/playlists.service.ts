import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '../../generated/prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { stat } from 'fs';

@Injectable()
export class PlaylistsService {
  constructor(private prisma: PrismaService) {}

  findOne(id: string) {
    return `This action returns a #${id} playlist`;
  }

  async playlist(
    playlistWhereUniqueInput: Prisma.playlistWhereUniqueInput,
  ): Promise<any> {
    try {
      const result = await this.prisma.playlist.findUnique({
        where: playlistWhereUniqueInput,
        select: {
          id: true,
          title: true,
          isPublic: true,
          isDefault: true,
          status: true,
          musics: {
            select: {
              index: true,
              music: {
                select: {
                  id: true,
                  title: true,
                  duration: true,
                  artists: {
                    select: {
                      title: true,
                    },
                  },
                },
              },
            },
          },
          playlistships: {
            where: {
              status: 'ACCEPTED',
            },
            select: {
              user: {
                select: {
                  id: true,
                  username: true,
                },
              },
            },
          },
        },
      });
      if (!result) {
        throw new NotFoundException('Playlist not found');
      }
      result['type'] = 'playlist';

      return result;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid playlist ID');
      }
      throw error;
    }
  }

  async create(data: Prisma.playlistCreateInput) {
    try {
      const newPlaylist = await this.prisma.playlist.create({
        data,
      });
      return newPlaylist;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid data for playlist creation');
      }
      throw error;
    }
  }
}
