import { SubscribeMessage, WebSocketGateway } from '@nestjs/websockets';
import { Socket } from 'socket.io';
import { DevicesService } from './devices.service';
import { PrismaService } from '../prisma/prisma.service';
import { WebSocketsService } from '../websockets/websockets.service';
import { BaseGateway } from '../websockets/base.gateway';

@WebSocketGateway()
export class DevicesGateway {
  constructor(
    private readonly devicesService: DevicesService,
    private readonly baseGateway: BaseGateway,
  ) {}

  private activeRooms = new Map<string, Set<string>>();

  @SubscribeMessage('connectToDevice')
  async handleSendToDevice(
    client: Socket,
    payload: { targetDeviceId: string },
  ) {
    const userId = client.data.userId;

    const canConnect = await this.devicesService.canConnectToDevice(
      userId,
      payload.targetDeviceId,
    );

    if (!canConnect) {
      client.emit('error', {
        message:
          'Cannot connect to target device (device not exist or invalid permission)',
      });
    }
    const isConnected = this.baseGateway.sendToDevice(
      payload.targetDeviceId,
      'hostRequest',
      { emitDevice: client.data.deviceId, emitUserId: userId },
    );

    if (!isConnected) {
      client.emit('error', { message: 'Target device not connected' });
      return;
    }
  }

  generateRoomName(deviceId: string): string {
    return 'room-' + deviceId;
  }

  @SubscribeMessage('hostResponse')
  async handleHostResponse(
    client: Socket,
    payload: { emitDevice: string; emitUserId: number },
  ) {
    const userId = client.data.userId;

    const canConnect = await this.devicesService.canConnectToDevice(
      payload.emitUserId,
      userId,
    );

    if (!canConnect) {
      client.emit('error', {
        message:
          'Cannot connect to target device (device not exist or invalid permission)',
      });
    }

    if (!this.activeRooms.has(this.generateRoomName(client.data.deviceId))) {
    }

    this.baseGateway.sendToDevice(payload.emitDevice, 'hostResponse', {
      // isAccepted: payload.isAccepted,
    });
  }
}
