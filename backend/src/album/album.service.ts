import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '../../generated/prisma/client';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class AlbumService {
  constructor(private prisma: PrismaService) {}

  async album(
    albumWhereUniqueInput: Prisma.albumWhereUniqueInput,
  ): Promise<any> {
    try {
      const result = await this.prisma.album.findUnique({
        where: albumWhereUniqueInput,
        select: {
          id: true,
          title: true,
          date: true,
          images: true,
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
          artists: {
            select: {
              title: true,
            },
          },
        },
      });
      if (!result) {
        throw new NotFoundException('Album not found');
      }
      result['type'] = 'album';

      return result;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid album ID');
      }
      throw error;
    }
  }
}
