import {
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { DevicesService } from './devices.service';
import { WebSocketsService } from '../websockets/websockets.service';
import { BaseGateway } from '../websockets/base.gateway';
import {
  PlaybackStateDto,
  PlaybackStateResponseDto,
  PlaybackStateResponseRejectDto,
} from './dto/playbackState.dto';

@WebSocketGateway()
export class DevicesGateway {
  @WebSocketServer() server!: Server;

  constructor(
    private readonly devicesService: DevicesService,
    private readonly baseGateway: BaseGateway,
    private readonly websocketsService: WebSocketsService,
  ) {}

  handleConnection(client: Socket) {
    client.on('disconnecting', () => {
      for (const roomName of Array.from(client.rooms)) {
        if (roomName.startsWith('room-')) {
          this.leaveRoom(client, roomName);
        }
      }
    });
  }

  @SubscribeMessage('connectToDevice')
  async handleSendToDevice(client: Socket, deviceId: string) {
    const userId = client.data.userId;

    const canConnect = await this.devicesService.canConnectToDevice(
      userId,
      deviceId,
    );

    if (canConnect === undefined) {
      client.emit('app_error', {
        message:
          'Cannot connect to target device (device not exist or invalid permission)',
      });
      return;
    }
    const isConnected = this.baseGateway.sendToDevice(deviceId, 'hostRequest', {
      emitDeviceID: client.data.deviceId,
      emitUserId: userId,
    });

    if (!isConnected) {
      client.emit('app_error', { message: 'Target device not connected' });
      return;
    }
  }

  generateRoomName(deviceId: string): string {
    return 'room-' + deviceId;
  }

  getDeviceIdFromRoomName(roomName: string): string {
    return roomName.replace('room-', '');
  }

  deleteRoom(roomName: string) {
    const room = this.server.sockets.adapter.rooms.get(roomName);
    if (room && room.size > 0) {
      this.server.to(roomName).emit('disconnectFromDevice', {
        deviceId: this.getDeviceIdFromRoomName(roomName),
      });
      this.server.socketsLeave(roomName);
    }
  }

  leaveRoom(client: Socket, roomName: string): boolean {
    const room = this.server.sockets.adapter.rooms.get(roomName);
    if (room && room.has(client.id)) {
      if (client.data.deviceId === this.getDeviceIdFromRoomName(roomName)) {
        this.deleteRoom(roomName);
      } else {
        this.server.to(roomName).emit('userDisconnected', {
          deviceId: this.getDeviceIdFromRoomName(roomName),
          userId: client.data.userId,
        });
        client.leave(roomName);

        if (room.size === 1) {
          this.deleteRoom(roomName);
        }
      }
      return true;
    } else {
      client.emit('app_error', { message: 'Not connected to the device' });
      return false;
    }
  }

  @SubscribeMessage('disconnectFromDevice')
  async handleDisconnectFromDevice(client: Socket, deviceId: string) {
    const roomName = this.generateRoomName(deviceId);

    if (this.leaveRoom(client, roomName)) {
      client.emit('disconnectedFromDevice', { deviceId: deviceId });
    }
  }

  private async joinDeviceToRoom(
    deviceId: string,
    roomName: string,
  ): Promise<boolean> {
    const socketId = this.websocketsService.getSocketByDeviceId(deviceId);
    if (!socketId) return false;

    const targetSocket = this.server.sockets.sockets.get(socketId);
    if (!targetSocket) return false;

    await targetSocket.join(roomName);
    return true;
  }

  @SubscribeMessage('hostResponse')
  async handleHostResponse(
    client: Socket,
    payload: {
      emitDeviceID: string;
      emitUserId: number;
      data: PlaybackStateDto | undefined;
      isAccepted: boolean;
    },
  ) {
    try {
      const canConnect = await this.devicesService.canConnectToDevice(
        payload.emitUserId,
        client.data.deviceId,
      );
      if (canConnect === undefined) {
        client.emit('app_error', {
          message:
            'Cannot connect to target device (device not exist or invalid permission)',
        });
        return;
      }
    } catch (error) {
      client.emit('app_error', {
        message:
          'Cannot connect to target device (device not exist or invalid permission)',
      });
      return;
    }

    if (!payload.isAccepted || !payload.data) {
      const playbackStateResponseReject: PlaybackStateResponseRejectDto = {
        deviceId: client.data.deviceId,
        isAccepted: false,
      };

      this.baseGateway.sendToDevice(
        payload.emitDeviceID,
        'hostResponse',
        playbackStateResponseReject,
      );
      return;
    }

    const roomName = this.generateRoomName(client.data.deviceId);

    const join = await this.joinDeviceToRoom(client.data.deviceId, roomName);
    if (!join) {
      client.emit('app_error', { message: 'Cannot connect to target device' });
      return;
    }

    const joined = await this.joinDeviceToRoom(payload.emitDeviceID, roomName);

    if (!joined) {
      client.emit('app_error', {
        message: 'Target device is no longer connected',
      });
      this.deleteRoom(roomName);
      return;
    }

    console.log(
      'Room:',
      roomName,
      'Size:',
      this.server.sockets.adapter.rooms.get(roomName)?.size,
    );

    const musicListObj = await this.devicesService.getMusicListFromIds(
      payload.data.musicListIds || [],
    );

    const playbackStateResponse: PlaybackStateResponseDto = {
      isPlaying: payload.data.isPlaying,
      currentTime: payload.data.currentTime,
      deviceId: client.data.deviceId,
      isAccepted: true,
      currentMusicId: payload.data.currentMusicId,
      musicList: musicListObj,
    };

    this.baseGateway.sendToDevice(
      payload.emitDeviceID,
      'hostResponse',
      playbackStateResponse,
    );
  }

  @SubscribeMessage('modifyData')
  async handleModifyData(client: Socket, payload: PlaybackStateDto) {
    const userId = client.data.userId;
    const userDeviceId = client.data.deviceId;

    if (!payload.deviceId) {
      client.emit('app_error', { message: 'Missing deviceId' });
      return;
    }

    if (userDeviceId !== payload.deviceId) {
      const canConnect = await this.devicesService.canConnectToDevice(
        userId,
        payload.deviceId,
      );
      if (canConnect === undefined) {
        client.emit('app_error', {
          message:
            'Cannot connect to target device (device not exist or invalid permission)',
        });
        return;
      }

      if (!canConnect.canModifyMusic && payload.musicListIds !== undefined) {
        client.emit('app_error', { message: 'No permission to modify music' });
        return;
      }

      if (!canConnect.canSeek && payload.currentTime !== undefined) {
        client.emit('app_error', { message: 'No permission to seek' });
        return;
      }

      if (
        !canConnect.canTogglePlayPause &&
        (payload.isPlaying !== undefined ||
          payload.currentMusicId !== undefined)
      ) {
        client.emit('app_error', {
          message: 'No permission to toggle play/pause',
        });
        return;
      }
    }
    const roomName = this.generateRoomName(payload.deviceId);
    const room = this.server.sockets.adapter.rooms.get(roomName);
    console.log('Room:', roomName, 'Size:', room?.size);
    console.log(room);
    if (!room || !room.has(client.id)) {
      client.emit('app_error', { message: 'Not connected to target device' });
      return;
    }
    const musicListObj = await this.devicesService.getMusicListFromIds(
      payload.musicListIds || [],
    );

    const playbackStateResponse: PlaybackStateResponseDto = {
      isPlaying: payload.isPlaying,
      currentTime: payload.currentTime,
      deviceId: payload.deviceId,
      currentMusicId: payload.currentMusicId,
      musicList: payload.musicListIds ? musicListObj : undefined,
    };

    this.server
      .to(roomName)
      .except(client.id)
      .emit('playback_state', playbackStateResponse, { userId: userId });
  }
}
