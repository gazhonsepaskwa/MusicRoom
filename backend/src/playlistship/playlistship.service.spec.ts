import { Test, TestingModule } from '@nestjs/testing';
import { PlaylistshipService } from './playlistship.service';

describe('PlaylistshipService', () => {
  let service: PlaylistshipService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [PlaylistshipService],
    }).compile();

    service = module.get<PlaylistshipService>(PlaylistshipService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
