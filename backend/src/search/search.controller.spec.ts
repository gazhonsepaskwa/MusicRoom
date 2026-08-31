import { Test, TestingModule } from '@nestjs/testing';
import { SearchController } from './search.controller';
import { describe, beforeEach, it } from 'node:test';

describe('SearchController', () => {
  let controller: SearchController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [SearchController],
    }).compile();

    controller = module.get<SearchController>(SearchController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
