import { PrismaService } from '../prisma/prisma.service.js';
import { user, Prisma } from '../../generated/prisma/client.js';
import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}

  async user(
    userWhereUniqueInput: Prisma.userWhereUniqueInput,
  ): Promise<user | null> {
    try {
      return this.prisma.user.findUnique({
        where: userWhereUniqueInput,
      });
    } catch (err) {
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

  async get_user(
    userWhereUniqueInput: Prisma.userWhereUniqueInput,
  ): Promise<any> {
    try {
      const result = await this.prisma.user.findUnique({
        where: userWhereUniqueInput,
        select: {
          id: true,
          username: true,
          email: true,
        },
      });
      if (!result) {
        throw new NotFoundException('User not found');
      }
      result['type'] = 'user';

      return result;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid user ID');
      }
      throw error;
    }
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

  async getUserProfile(userId: number, requesterId: number): Promise<any> {
	const user = await this.prisma.user.findUnique({
	  where: { id: userId },
	  select: {
		id: true,
		username: true,
		email: true,
	  },
	});
	return user;
  }
}
