import { BadRequestException, forwardRef, Inject, Injectable, InternalServerErrorException, UnauthorizedException } from '@nestjs/common';
import { NotificationsService } from '../notifications/notifications.service';
import { UsersService } from '../users/users.service';
import { PrismaService } from '../prisma/prisma.service';
import { invitationStatus, playlistship, Prisma } from '../../generated/prisma/browser';
import { PlaylistsService } from '../playlists/playlists.service';
import { PlaylistshipAnswerDto, PlaylistshipDto } from './dto/playlistship.dto';

@Injectable()
export class PlaylistshipService {
	constructor(
		private prisma: PrismaService, 
		@Inject(forwardRef(() => NotificationsService))
		private notificationsService: NotificationsService,
		private usersService: UsersService,
		private playlistsService: PlaylistsService
	) {}

	async sendPlaylistInvitation(playlistshipDto: PlaylistshipDto, senderId: number): Promise<void> {
		await this.checkOwnership(senderId, playlistshipDto.playlistId);
		if (await this.playlistshipExists(playlistshipDto.playlistId, playlistshipDto.addresseeId)) {
			throw new BadRequestException('User Already has access to the playlist');
		}
		await this.createPlaylistship({
			playlistId: playlistshipDto.playlistId,
			addresseeId: playlistshipDto.addresseeId,
		})
		const playlistName = (await this.playlistsService.findOne({id: playlistshipDto.playlistId}))?.title;
		this.notificationsService.sendNotification(
			playlistshipDto.addresseeId,
			playlistshipDto.playlistId,
			{
				websoketEvent: "playlist-invitation",
				FireBaseTitle: `Playlist Invitation For ${playlistName}`,
				FirebaseMessage: `You have been invited to join ${playlistName}. Will you accept it? This message will self-Destruct in 3... 2... 1...`,
			}
		)
	}

	async createPlaylistship(data: Prisma.playlistshipUncheckedCreateInput): Promise<playlistship> {
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
		let playlistship = await this.playlistship({playlistId_addresseeId: {playlistId, addresseeId}})
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

	async updateManpyPlaylistshipStatus(where : Prisma.playlistshipWhereInput, data: Prisma.playlistshipUpdateInput){
		return await this.prisma.playlistship.updateMany({
			data,
			where,
		})
	}

	async deletePlaylistship(playlistshipDto: PlaylistshipDto, userId: number) {
		if (!await this.playlistsService.findOne({id: playlistshipDto.playlistId}))
			throw new BadRequestException("Playlist Not FOund");
		if (userId != playlistshipDto.addresseeId)
			await this.checkOwnership(userId, playlistshipDto.playlistId);
		await this.prisma.playlistship.delete({
			where: { playlistId_addresseeId: { playlistId: playlistshipDto.playlistId, addresseeId: playlistshipDto.addresseeId } }
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
		if (!await this.playlistsService.findOne({id}))
			throw new BadRequestException("Playlist Not FOund");
		return await this.prisma.playlistship.findMany({
			where: {playlistId: id, status: 'ACCEPTED'}
		});
	}

	async answerPlaylistInvitation(playlistshipDto: PlaylistshipAnswerDto, userId: number): Promise<playlistship> {
		const playlistship = await this.updatePlaylistshipStatus(
			playlistshipDto.playlistId, 
			userId, 
			playlistshipDto.status
		);
		if (!playlistship)
			throw new InternalServerErrorException("Playlistship was not recognized")
		return playlistship;
	}

	async checkOwnership(userId: number, playlistId: number) {
		const playlist = await this.playlistsService.findOne({id: playlistId});
		if (!playlist)
			throw new BadRequestException("Playlist Not Found");
		if (playlist?.userId != userId)
			throw new UnauthorizedException("You do not own or have access to this playlist!");
	}
}
