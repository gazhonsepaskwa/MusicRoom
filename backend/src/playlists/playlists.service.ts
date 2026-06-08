import { Injectable } from '@nestjs/common';

@Injectable()
export class PlaylistsService {
  findOne(id: string) {
    return `This action returns a #${id} playlist`;
  }
}
