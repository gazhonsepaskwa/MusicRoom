import { PrismaService } from '../prisma/prisma.service.js';
import { user, Prisma, visibilityStatus, invitationStatus } from '../../generated/prisma/client.js';
import {
  BadRequestException,
  Injectable,
  NotFoundException,
  Inject,
  forwardRef
} from '@nestjs/common';
import { PlaylistsService } from '../playlists/playlists.service.js';
import { ChangePasswordDto, UserProfileResponseDto, UserResponseDto } from './dto/user.dto.js';
import { FriendshipService } from '../friendship/friendship.service.js';
import * as bcrypt from 'bcrypt';
import { AuthService } from '../auth/auth.service.js';


export const VisibilityLevel: Record<visibilityStatus, number> = {
  PRIVATE: 0,
  FRIEND: 1,
  PUBLIC: 2,
};

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService,
	private playlistsService: PlaylistsService,
	@Inject(forwardRef(() => FriendshipService))
	private friendshipService: FriendshipService,
	@Inject(forwardRef(() => AuthService))
	private authService: AuthService
  ) {}

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

  async encryptPassword(password: string): Promise<string> {
	const salt = await bcrypt.genSalt();
	return await bcrypt.hash(password, salt);
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

  async deleteUser(where: Prisma.userWhereUniqueInput): Promise<UserResponseDto> {
    return await this.prisma.user.delete({
      where,
	  select: {
		id: true,
		username: true,
		email: true
	  }
    });
  }

	showProfileItem(status: visibilityStatus, requester: visibilityStatus): boolean {
		const levelRequired = VisibilityLevel[status];
		const requesterlevel = VisibilityLevel[requester];

		return requesterlevel <= levelRequired;
	}

  async getUserProfile(userId: number, requesterId: number): Promise<any> {
	const user = await this.user({ id: userId });
	const isFriend = await this.friendshipService.isFriend(requesterId, userId);
	const visibilty = userId == requesterId ? visibilityStatus.PRIVATE : (isFriend == invitationStatus.ACCEPTED ? visibilityStatus.FRIEND : visibilityStatus.PUBLIC)
	if (!user) {
	  throw new NotFoundException('User not found');
	}
	let result: UserProfileResponseDto;
	const ownedPlaylists = await this.playlistsService.getOwned(userId, requesterId);
	const invitedPlaylists = await this.playlistsService.getInvited(userId, requesterId);
	const friendsCount = await this.friendshipService.getFriendsCount(userId);
	const { ownedPlaylistsCount, invitedPlaylistsCount } = await this.playlistsService.getPlaylistCounts(userId);
	result = { 
		id: user.id,
		username: user.username,
		email: this.showProfileItem(user.showAddress, visibilty) ? user.email : null,
		friends: this.showProfileItem(user.showFriends, visibilty) ? friendsCount : null,
		playlists: ownedPlaylistsCount + invitedPlaylistsCount,
		invitedPlaylistsNbr: invitedPlaylistsCount,
		ownedPlaylistsNbr: ownedPlaylistsCount,
		isFriend: isFriend ? isFriend : null,
		firstPreferedMusicId: this.showProfileItem(user.showPreferedMusics, visibilty) ? user.firstPreferredMusicId : null,
		secondPreferedMusicId: this.showProfileItem(user.showPreferedMusics, visibilty) ? user.secondPreferredMusicId : null,
		thirdPreferedMusicId: this.showProfileItem(user.showPreferedMusics, visibilty) ? user.thirdPreferredMusicId : null,
		ownedPlaylists: this.showProfileItem(user.showCreatedPlaylist, visibilty) ? ownedPlaylists : null,
		invitedPlaylists: this.showProfileItem(user.showInvitedPlaylist, visibilty) ? invitedPlaylists : null,
		showAddress: visibilty == visibilityStatus.PRIVATE ? user.showAddress : null,
		showCreatedPlaylist: visibilty == visibilityStatus.PRIVATE ? user.showCreatedPlaylist : null,
		showFriends: visibilty == visibilityStatus.PRIVATE ? user.showFriends : null,
		showInvitedPlaylist: visibilty == visibilityStatus.PRIVATE ? user.showInvitedPlaylist : null,
		showPreferedMusics: visibilty == visibilityStatus.PRIVATE ? user.showPreferedMusics : null,
		isYou: userId == requesterId
	};
	return result;
  }

  async changePassword(userId: number, data: ChangePasswordDto) {
	const user = (await this.user({id: userId}))!;
	await this.authService.confirmPassword(user, data.oldPassword)
	data.newPassword = await this.encryptPassword(data.newPassword);
	await this.updateUser({ where:{id: userId}, data: {password: data.newPassword}});
  }
}
