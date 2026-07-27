import { BadRequestException, Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { deviceship } from '../../generated/prisma/client.js';
import { WebSocketsService } from '../websockets/websockets.service';

@Injectable()
export class DevicesService {
  constructor(
    private prisma: PrismaService,
    private websocketsService: WebSocketsService,
  ) {}
  async addDevice(ownerId: number, id: string, name: string) {
    const device = await this.prisma.device.upsert({
      where: {
        id,
      },
      create: {
        id,
        name,
        ownerId,
      },
      update: {
        name,
      },
    });
    return device;
  }

  async isAccessibleDevice(userId: number, deviceId: string) {
    const device = await this.prisma.device.findUnique({
      where: {
        id: deviceId,
      },
    });

    if (!device || device.ownerId !== userId) return false;
    return true;
  }
  async updateDeviceName(id: string, userId: number, name: string) {
    const isDeviceOwner = await this.isAccessibleDevice(userId, id);
    if (!isDeviceOwner)
      throw new BadRequestException(
        'Invalid device or you are not the owner of this device',
      );
    const device = await this.prisma.device.update({
      where: {
        id,
      },
      data: {
        name,
      },
    });
    return device;
  }

  async updateDevicePermission(
    id: string,
    userId: number,
    friendId: number,
    data: {
      name?: string;
      canSeek?: boolean;
      canTogglePlayPause?: boolean;
      canModifyMusic?: boolean;
    },
  ) {
    if (userId === friendId)
      throw new BadRequestException('You cannot add yourself as a friend');

    const isDeviceOwner = await this.isAccessibleDevice(userId, id);

    if (!isDeviceOwner)
      throw new BadRequestException(
        'Invalid device or you are not the owner of this device',
      );

    const device = await this.prisma.deviceship.upsert({
      where: {
        deviceId_userId: {
          deviceId: id,
          userId: friendId,
        },
      },
      create: {
        deviceId: id,
        userId: friendId,
        ...data,
      },
      update: {
        ...data,
      },
    });
    return device;
  }

  async deviceship(deviceId: string, userId: number) : Promise<deviceship> {
	const deviceship = await this.prisma.deviceship.findUnique({
		where: {
			deviceId_userId: {
				deviceId,
				userId
			}
		}
	});
	if (!deviceship)
		throw new BadRequestException("No Deviceship Found between user and device");
	return deviceship;
  }

  async deleteDevice(id: string, userId: number) {
    const isDeviceOwner = await this.isAccessibleDevice(userId, id);
    if (!isDeviceOwner)
      throw new BadRequestException(
        'Invalid device or you are not the owner of this device',
      );
    const device = await this.prisma.device.delete({
      where: {
        id,
      },
    });
    return device;
  }

  async getAvailableDevices(userId: number) {
    const devices = await this.prisma.deviceship.findMany({
      where: {
        userId,
      },
	  select: {
		deviceId: true,
		device: {
			select: {
				name: true,
			}
		},
		userId: true,
		createdAt: true,
		canSeek: true,
		canTogglePlayPause: true,
		canModifyMusic: true,
	  }
    });

    return devices.map((device) => ({
      ...device,
      isOnlineDevice: this.websocketsService.isOnlineDevice(device.deviceId),
    }));
  }

  async getUserDevices(userId: number) {
    const devices = await this.prisma.device.findMany({
      where: {
        ownerId: userId,
      },
    });
    return devices;
  }

  async canConnectToDevice(
    userId: number,
    deviceId: string,
  ): Promise<
    | undefined
    | { canSeek: boolean; canTogglePlayPause: boolean; canModifyMusic: boolean }
  > {
    const deviceship = await this.prisma.deviceship.findUnique({
      where: {
        deviceId_userId: {
          userId,
          deviceId,
        },
      },
    });

    if (!deviceship) {
      return undefined;
    }

    if (
      !deviceship.canModifyMusic &&
      !deviceship.canSeek &&
      !deviceship.canTogglePlayPause
    ) {
      return undefined;
    }

    return deviceship;
  }
}
