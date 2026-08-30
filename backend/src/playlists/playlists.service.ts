import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '../../generated/prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { playlist } from '../../generated/prisma/browser';
import { PlaylistVersionResponseDto } from './dto/playlists.dto';
import { title } from 'process';

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
		console.error('Error creating playlist:', error);
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
	  const playlist = await this.findOne(params.where);
	  if (playlist && playlist.isDefault && data.isPublic)
		throw new BadRequestException("Favorite Playlist can not be public");
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

  async delete(where: Prisma.playlistWhereUniqueInput, userId: number) {
    try {
	  const playlist = await this.prisma.playlist.findUnique({
		where,
		select: { userId: true, isDefault: true },
	  });

	  if (!playlist) {
		throw new NotFoundException('Playlist not found');
	  }
	  if (playlist.userId !== userId) {
		throw new BadRequestException('You are not the owner of this playlist');
	  }
	  if (playlist.isDefault) {
		throw new BadRequestException('Default playlists cannot be deleted');
	  }
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

  async deleteAllUserPlaylists(userId: number) {
	try {
	  const deletedPlaylists = await this.prisma.playlist.deleteMany({
		where: { userId },
	  });
	} catch (error) {
	  if (error instanceof Prisma.PrismaClientKnownRequestError) {
		throw new BadRequestException('Invalid user ID for playlist deletion');
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

  async addMusic(playlistId: number, songId: number): Promise<PlaylistVersionResponseDto | void> {
	try {
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

		const version =await this.incrementPlaylistVersion(playlistId);
		return {
			playlistId: playlistId,
			musicId: songId,
			version: version,
		};
	} catch (error) {
		if (error instanceof Prisma.PrismaClientKnownRequestError) {
			throw new BadRequestException('Invalid data for adding music to playlist');
		}
		throw error;
	}
  }

  async removeMusic(playlistId: number, songId: number): Promise<PlaylistVersionResponseDto | void> {
	try {
		const playlist = await this.prisma.playlist.findUnique({
		where: { id: playlistId },
		include: { musics: true },
		});

			if (!playlist) {
		throw new NotFoundException('Playlist not found');
		}

		const existingMusic = playlist.musics.find((pm) => pm.musicId === songId);

		if (!existingMusic) {
		throw new BadRequestException('Music not in playlist');
		}

		await this.prisma.playlistMusic.delete({
		where: {
			playlistId_musicId: {
			playlistId,
			musicId: songId,
			},
		},
		});

		const version = await this.incrementPlaylistVersion(playlistId);
		return {
			playlistId: playlistId,
			musicId: songId,
			version: version,
		};
	} catch (error) {
		if (error instanceof Prisma.PrismaClientKnownRequestError) {
			throw new BadRequestException('Invalid data for adding music to playlist');
		}
		throw error;
	}
  }

  async canJoinPlaylist(playlistId: number, userId: number): Promise<number | undefined> {
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
      return undefined;
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
	  include: {
		musics: {
			include: {
					music: {
						select: {
							duration: true,
						},
					},
				},
			},
		},
	});

    if (!personnalPlaylists) {
      throw new NotFoundException('No personnal playlists found');
    }

    return personnalPlaylists.map((playlist) => ({
		id: playlist.id,
		title: playlist.title,
		songs: playlist.musics.length,
		duration: playlist.musics.reduce(
			(sum, musics) => sum + musics.music.duration,
			0,
		),
		}));
  }

  async getOwned(userId: number, requesterId: number) {
	const ownedPlaylists = await this.prisma.playlist.findMany({
	  where: {
		AND: [
		{ userId },
		{
			OR: [
				{ userId },
				{ isPublic: true },
				{
					playlistships: {
						some: {
							addresseeId: requesterId,
							status: 'ACCEPTED',
						},
					},
				},
			],
		}
		]},
		include: {
		musics: {
			include: {
					music: {
						select: {
							duration: true,
						},
					},
				},
			},
		},
	});
	return ownedPlaylists.map((playlist) => ({
		id: playlist.id,
		title: playlist.title,
		songs: playlist.musics.length,
		duration: playlist.musics.reduce(
			(sum, musics) => sum + musics.music.duration,
			0,
		),
		}));
  }

  async getInvited(userId: number, requesterId: number) {
	const invitedPlaylists = await this.prisma.playlist.findMany({
	  where: {
		AND: [
		{
			playlistships: {
			some: {
				addresseeId: userId,
				status: 'ACCEPTED',
			},
		},
		},
		{
			playlistships: {
			some: {
				addresseeId: requesterId,
				status: 'ACCEPTED',
			},
		},
		}
	  ]},
	   include: {
		musics: {
			include: {
					music: {
						select: {
							duration: true,
						},
					},
				},
			},
		},
	});
	return invitedPlaylists.map((playlist) => ({
		id: playlist.id,
		title: playlist.title,
		songs: playlist.musics.length,
		duration: playlist.musics.reduce(
			(sum, musics) => sum + musics.music.duration,
			0,
		),
		}));
  }

  async canAccess(playlistId: number, userId: number): Promise<boolean> {
	const playlist = await this.prisma.playlist.findUnique({
		where: {
		  id: playlistId,
		OR: [
			{ userId },
			{playlistships: {
				some: {
					addresseeId: userId,
					status: 'ACCEPTED',
				},
			}},
		],
	   },
	  select: { userId: true },
	});
	return playlist?.userId === userId;
  }

  async getPlaylistCounts(userId: number): Promise<{ ownedPlaylistsCount: number; invitedPlaylistsCount: number }> {
	const ownedPlaylistsCount = await this.prisma.playlist.count({
		where: {
			userId: userId,
		},
		});
	const invitedPlaylistsCount = await this.prisma.playlist.count({
		where: {
			playlistships: {
			some: {
				addresseeId: userId,
			},
			},
		},
		});
		return { ownedPlaylistsCount, invitedPlaylistsCount };
	}
}
