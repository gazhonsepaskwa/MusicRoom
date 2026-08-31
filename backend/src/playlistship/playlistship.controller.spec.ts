import { Test, TestingModule } from '@nestjs/testing';
import { PlaylistshipController } from './playlistship.controller';
import { PlaylistshipService } from './playlistship.service';

describe('PlaylistshipController', () => {
  let controller: PlaylistshipController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PlaylistshipController],
      providers: [PlaylistshipService],
    }).compile();

    controller = module.get<PlaylistshipController>(PlaylistshipController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
