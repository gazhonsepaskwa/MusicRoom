import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';
import { ApiOkResponse } from '@nestjs/swagger';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @ApiOkResponse({
  schema: {
    type: 'object',
    properties: {
      server_name: {
        type: 'string',
        example: 'musicroom',
      },
    },
  },
  })
  @Get('server-check')
  getServerResponse() {
	return {server_name: "musicroom"};
  }
}
