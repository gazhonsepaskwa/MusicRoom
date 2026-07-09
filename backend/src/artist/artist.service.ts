import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Prisma, artist } from '../../generated/prisma/client';

@Injectable()
export class ArtistService {
  constructor(private prisma: PrismaService) {}

  async artist(
    artistWhereUniqueInput: Prisma.artistWhereUniqueInput,
  ): Promise<any> {
    try {
      const result = await this.prisma.artist.findUnique({
        where: artistWhereUniqueInput,
        select: {
          id: true,
          title: true,
          images: true,
          albums: {
            select: {
			  id: true,
              title: true,
              date: true,
              images: true,
            },
          },
        },
      });
      if (!result) {
        throw new NotFoundException('Artist not found');
      }

	  result['type'] = 'artist';
      return result;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid artist ID');
      }
      throw error;
    }
  }

  async artistMusic(artistWhereUniqueInput: Prisma.artistWhereUniqueInput): Promise<any> {
	try {
	  const result = await this.prisma.artist.findUnique({
		where: artistWhereUniqueInput,
		select: {
		  id: true,
		  title: true,
		  images: true,
		  musics: {
			select: {
			  id: true,
			  title: true,
			  duration: true,
			}},
	  }
	});
	if (!result) {
	  throw new NotFoundException('Artist not found');
	}

	result['type'] = 'artist';
	return result;
	}
	catch (error) {
	  if (error instanceof Prisma.PrismaClientKnownRequestError) {
		throw new BadRequestException('Invalid artist ID');
	  }
	  throw error;
	}
  }
}
