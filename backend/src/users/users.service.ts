import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service.js';
import { user, Prisma } from '../../generated/prisma/client.js';
import { ArtistService } from '../artist/artist.service.js';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService, private artistService: ArtistService) {}

  async user(userWhereUniqueInput: Prisma.userWhereUniqueInput): Promise<user | null> {
	try {

		return this.prisma.user.findUnique({
			where: userWhereUniqueInput,
		});
	}
	catch (err) {
		return null;
	}
  }

  async users(params: {
    skip?: number;
    take?: number;
    cursor?: Prisma.userWhereUniqueInput;
    where?: Prisma.userWhereInput;
    orderBy?: Prisma.userOrderByWithRelationInput;
  }): Promise<user[]> {
    const { skip, take, cursor, where, orderBy } = params;
    return this.prisma.user.findMany({
      skip,
      take,
      cursor,
      where,
      orderBy,
    });
  }

  async createUser(data: Prisma.userCreateInput): Promise<user> {
    return this.prisma.user.create({
      data,
    });
  }

  async updateUser(params: {
    where: Prisma.userWhereUniqueInput;
    data: Prisma.userUpdateInput;
  }): Promise<user> {
    const { where, data } = params;
    return this.prisma.user.update({
      data,
      where,
    });
  }

  async deleteUser(where: Prisma.userWhereUniqueInput): Promise<user> {
    return this.prisma.user.delete({
      where,
    });
  }

  async setPreferredArtist(index: number, userId: number, artistId?: number) {
	if (artistId) {
		const artist = await this.artistService.artist({ id: artistId });
		if (!artist) {
			throw new Error(`Artist with id ${artistId} does not exist`);
		}
	}
	switch (index) {
		case 1:
			return this.prisma.user.update({
				where: { id: userId },
				data: { firstPreferredArtistId: artistId ? artistId : null },
			});
		case 2:
			return this.prisma.user.update({
				where: { id: userId },
				data: { secondPreferredArtistId: artistId ? artistId : null },
			});
		case 3:
			return this.prisma.user.update({
				where: { id: userId },
				data: { thirdPreferredArtistId: artistId ? artistId : null },
			});
		default:
			throw new Error('Index must be between 1 and 3');
	}
  }
}
