import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class WebSocketsService {
  constructor(private prisma: PrismaService) {}

  private userSockets = new Map<string, Set<string>>();

  addSocket(userId: string, socketId: string) {
    if (!this.userSockets.has(userId)) {
      this.userSockets.set(userId, new Set());
    }
    this.userSockets.get(userId)!.add(socketId);
  }

  removeSocket(userId: string, socketId: string) {
    const sockets = this.userSockets.get(userId);
    if (!sockets) return;

    sockets.delete(socketId);

    if (sockets.size === 0) {
      this.userSockets.delete(userId);
    }
  }

  isOnline(userId: string): boolean {
    return this.userSockets.has(userId);
  }

  getOnlineUsers(): string[] {
    return [...this.userSockets.keys()];
  }

  getUserSockets(userId: string): Set<string> | undefined {
    return this.userSockets.get(userId);
  }

  getSocketCount(userId: string): number {
    return this.userSockets.get(userId)?.size ?? 0;
  }
}
