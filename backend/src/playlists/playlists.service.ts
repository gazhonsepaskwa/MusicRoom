import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '../../generated/prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { playlist } from '../../generated/prisma/browser';

@Injectable()
export class PlaylistsService {
  constructor(private prisma: PrismaService) {}

  async findOne(playlistWhereUniqueInput: Prisma.playlistWhereUniqueInput): Promise<playlist | null> {
    return await this.prisma.playlist.findUnique({
		where: playlistWhereUniqueInput,
	});
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

  async update(params: {
    where: Prisma.playlistWhereUniqueInput;
    data: Prisma.playlistUpdateInput;
  }) {
    const { where, data } = params;
    try {
      const updatedPlaylist = await this.prisma.playlist.update({
        where,
        data,
      });
      return updatedPlaylist;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid data for playlist update');
      }
      throw error;
    }
  }

  async delete(where: Prisma.playlistWhereUniqueInput) {
    try {
      const deletedPlaylist = await this.prisma.playlist.delete({
        where,
      });
      return deletedPlaylist;
    } catch (error) {
      if (error instanceof Prisma.PrismaClientKnownRequestError) {
        throw new BadRequestException('Invalid playlist ID for deletion');
      }
      throw error;
    }
  }

  async getPlaylistVersion(playlistId: number): Promise<number> {
    const playlist = await this.prisma.playlist.findUnique({
      where: { id: playlistId },
      select: { version: true },
    });
    return playlist ? playlist.version : 0;
  }

  async incrementPlaylistVersion(playlistId: number): Promise<number> {
    const updatedPlaylist = await this.prisma.playlist.update({
      where: { id: playlistId },
      data: { version: { increment: 1 } },
    });
    return updatedPlaylist.version;
  }

  async addMusic(playlistId: number, songId: number): Promise<number | void> {
    const playlist = await this.prisma.playlist.findUnique({
      where: { id: playlistId },
      include: { musics: true },
    });

    if (!playlist) {
      throw new NotFoundException('Playlist not found');
    }

    const existingMusic = playlist.musics.find((pm) => pm.musicId === songId);

    if (existingMusic) {
      throw new BadRequestException('Music already in playlist');
    }

    await this.prisma.playlistMusic.create({
      data: {
        playlistId,
        musicId: songId,
        index: playlist.musics.length,
      },
    });

    return await this.incrementPlaylistVersion(playlistId);
  }

  async canJoinPlaylist(playlistId: number, userId: number): Promise<number> {
    const playlist = await this.prisma.playlist.findUnique({
      where: {
        id: playlistId,
        OR: [
          { isPublic: true },
          {
            playlistships: {
              some: {
                addresseeId: userId,
                status: 'ACCEPTED',
              },
            },
          },
        ],
      },
    });

    if (!playlist) {
      throw new NotFoundException(
        `Playlist ${playlistId} not found or user not authorized`,
      );
    }

    return await this.getPlaylistVersion(playlistId);
  }

  async moveMusic(
    playlistId: number,
    musicId: number,
    newIndex: number,
  ): Promise<number | void> {
    const playlist = await this.prisma.playlist.findUnique({
      where: { id: playlistId },
      include: {
        musics: {
          orderBy: { index: 'asc' },
        },
      },
    });

    if (!playlist) {
      throw new NotFoundException('Playlist not found');
    }

    const musicToMove = playlist.musics.find((pm) => pm.musicId === musicId);

    if (!musicToMove) {
      throw new NotFoundException('Music not found in playlist');
    }

    const oldIndex = musicToMove.index;

    if (
      newIndex < 0 ||
      newIndex >= playlist.musics.length ||
      newIndex === oldIndex
    ) {
      throw new BadRequestException('Invalid new index for music');
    }

    try {
      await this.prisma.$transaction(async (prisma) => {
        if (newIndex < oldIndex) {
          await prisma.playlistMusic.updateMany({
            where: {
              playlistId,
              index: {
                gte: newIndex,
                lt: oldIndex,
              },
            },
            data: {
              index: { increment: 1 },
            },
          });
        } else if (newIndex > oldIndex) {
          await prisma.playlistMusic.updateMany({
            where: {
              playlistId,
              index: {
                gt: oldIndex,
                lte: newIndex,
              },
            },
            data: {
              index: { decrement: 1 },
            },
          });
        }

        await prisma.playlistMusic.update({
          where: {
            playlistId_musicId: {
              playlistId,
              musicId,
            },
          },
          data: {
            index: newIndex,
          },
        });
      });
    } catch (error) {
      console.log(
        `Failed to move music ${musicId} in playlist ${playlistId} to index ${newIndex}: ${error}`,
      );
      throw error;
    }

    return await this.incrementPlaylistVersion(playlistId);
  }

  async getPersonnal(userId: number) {
    const personnalPlaylists = await this.prisma.playlist.findMany({
      where: {
        OR: [
          { userId },
          {
            playlistships: {
              some: {
                addresseeId: userId,
                status: 'ACCEPTED',
              },
            },
          },
        ],
      },
      select: {
        id: true,
      },
    });

    if (!personnalPlaylists) {
      throw new NotFoundException('No personnal playlists found');
    }

    const ret = await Promise.all(
      personnalPlaylists.map(async (playlist) => {
        return await this.playlist(playlist);
      }),
    );

    return ret;
  }
}
