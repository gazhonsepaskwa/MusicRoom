import { BadRequestException, forwardRef, Inject, Injectable, InternalServerErrorException } from '@nestjs/common';
import { NotificationsService } from '../notifications/notifications.service';
import { UsersService } from '../users/users.service';
import { PrismaService } from '../prisma/prisma.service';
import { invitationStatus, playlistship, Prisma } from '../../generated/prisma/browser';
import { PlaylistsService } from '../playlists/playlists.service';
import { PlaylistshipDto } from './dto/playlistship.dto';

@Injectable()
export class PlaylistshipService {
	constructor(
		private prisma: PrismaService, 
		@Inject(forwardRef(() => NotificationsService))
		private notificationsService: NotificationsService,
		private usersService: UsersService,
		private playlistsService: PlaylistsService
	) {}

	async sendPlaylistInvitation(playlistId: number, receiverId: number): Promise<void> {
		if (await this.playlistshipExists(playlistId, receiverId)) {
			throw new Error('User Already has access to the playlist');
		}
		this.createPlaylistship({
			playlistId: playlistId,
			addresseeId: receiverId,
		})
		const playlistName = (await this.playlistsService.playlist({id: playlistId})).title;
		this.notificationsService.sendNotification(
			receiverId,
			playlistId,
			{
				websoketEvent: "playlist-invitation",
				FireBaseTitle: `Playlist Invitation For ${playlistName}`,
				FirebaseMessage: `You have been invited to join ${playlistName}. Will you accept it? This message will self-Destruct in 3... 2... 1...`,
			}
		)
		console.log(`Playlist invitation sent for ${playlistId} to ${receiverId}`);
	}

	async createPlaylistship(data: Prisma.playlistshipUncheckedCreateInput): Promise<playlistship> {
		console.log(`${data.addresseeId} has now access to playlist ${data.playlistId}`);
		if (await this.playlistsService.playlist({id: data.playlistId}) == null)
			throw new BadRequestException("PlaylistId not found");
		if (await this.usersService.user({id: data.addresseeId}) == null)
			throw new BadRequestException("User Not Found");
		let playlistship = await this.playlistship({playlistId_addresseeId: {playlistId: data.playlistId, addresseeId: data.addresseeId}});
		if (playlistship)
			return playlistship;
		return this.prisma.playlistship.create({
			data,
		});
	}

	async playlistship(data: Prisma.playlistshipWhereUniqueInput): Promise<playlistship | null > {
		return  await this.prisma.playlistship.findUnique({
			where: data,
		});
	}

	async playlistshipExists(playlistId: number, addresseeId: number): Promise<boolean> {
		let playlistship = this.playlistship({playlistId_addresseeId: {playlistId, addresseeId}})
		return playlistship !== null;
	}

	async updatePlaylistshipStatus(
		playlistId: number,
		addresseeId: number,
		status: invitationStatus,
	): Promise<playlistship> {
		return this.prisma.playlistship.update({
			where: { playlistId_addresseeId: { playlistId, addresseeId } },
			data: { status },
		});
	}

	async deletePlaylistship(playlistId: number, addresseeId: number) {
		await this.prisma.playlistship.delete({
			where: { playlistId_addresseeId: { playlistId, addresseeId } }
		});
	}

	async getPlaylistInvitations(userId: number, status?: invitationStatus[]): Promise<playlistship[]> {
		const where: Prisma.playlistshipWhereInput = {
			addresseeId: userId ,
		};

		if (status?.length) {
			where.status = {
				in: status,
			};
		}
		return this.prisma.playlistship.findMany({
			where,
		});
	}

	async getPlaylistUsers(id: number): Promise<playlistship[] | null>{
		return await this.prisma.playlistship.findMany({
			where: {playlistId: id, status: 'ACCEPTED'}
		})
	}

	async answerPlaylistInvitation(playlistshipDto: PlaylistshipDto): Promise<playlistship> {
		if (!playlistshipDto.status)
			throw new BadRequestException("Answer Needed for Playlist Invitation")
		const playlistship = await this.updatePlaylistshipStatus(
			playlistshipDto.playlistId, 
			playlistshipDto.addresseeId, 
			playlistshipDto.status
		)
		if (!playlistship)
			throw new InternalServerErrorException("Playlistship was not recognized")
		return playlistship
	}
}
