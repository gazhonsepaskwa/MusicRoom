import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class WebSocketsService {
  constructor(private prisma: PrismaService) {}

  private socketDevice = new Map<string, string>();
  private deviceSocket = new Map<string, string>();
  private userSockets = new Map<number, Set<string>>();

  addSocket(userId: number, socketId: string, deviceId: string) {
    if (!this.userSockets.has(userId)) {
      this.userSockets.set(userId, new Set());
    }
    this.userSockets.get(userId)!.add(socketId);
    this.socketDevice.set(socketId, deviceId);
    this.deviceSocket.set(deviceId, socketId);
  }

  removeSocket(userId: number, socketId: string) {
    const sockets = this.userSockets.get(userId);
    if (!sockets) return;

    sockets.delete(socketId);
    const deviceId = this.socketDevice.get(socketId);
    if (deviceId) {
      this.deviceSocket.delete(deviceId);
      this.socketDevice.delete(socketId);
    }

    if (sockets.size === 0) {
      this.userSockets.delete(userId);
    }
  }

  getSocketByDeviceId(deviceId: string): string | undefined {
    return this.deviceSocket.get(deviceId);
  }

  isOnlineUser(userId: number): boolean {
    return this.userSockets.has(userId);
  }

  isOnlineDevice(deviceId: string): boolean {
    return this.deviceSocket.has(deviceId);
  }

  isOnlineSocket(socketId: string): boolean {
    return this.socketDevice.has(socketId);
  }

  getOnlineUsers(): number[] {
    return [...this.userSockets.keys()];
  }

  getUserSockets(userId: number): Set<string> | undefined {
    return this.userSockets.get(userId);
  }

  getSocketCount(userId: number): number {
    return this.userSockets.get(userId)?.size ?? 0;
  }
}
