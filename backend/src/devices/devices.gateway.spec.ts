import { Test, TestingModule } from '@nestjs/testing';
import { DevicesGateway } from './devices.gateway';

describe('DevicesGateway', () => {
  let gateway: DevicesGateway;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [DevicesGateway],
    }).compile();

    gateway = module.get<DevicesGateway>(DevicesGateway);
  });

  it('should be defined', () => {
    expect(gateway).toBeDefined();
  });
});
