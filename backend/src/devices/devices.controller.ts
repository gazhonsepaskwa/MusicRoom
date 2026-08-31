import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
} from '@nestjs/common';
import { ApiBody, ApiOkResponse, ApiParam, ApiTags } from '@nestjs/swagger';
import { DevicesService } from './devices.service';
import { UpdatePermissionDto } from './dto/updatePermission.dto';
import { UpdateNameDto } from './dto/updateName.dto';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import {
  AvailableDeviceResponseDto,
  DeviceRequestDto,
  DeviceResponseDto,
  DeviceshipResponseDto,
} from './dto/device.dto';

@ApiTags('devices')
@Controller('devices')
export class DevicesController {
  constructor(private readonly devicesService: DevicesService) {}

  @ApiBody({ type: UpdatePermissionDto })
  @ApiOkResponse({ type: DeviceshipResponseDto })
  @Patch('update_permissions')
  async updateDevicesPerm(
    @CurrentUser() userId: number,
    @Body() updatePermissionDto: UpdatePermissionDto,
  ) {
    const { id, friendId, canSeek, canTogglePlayPause, canModifyMusic } =
      updatePermissionDto;

    return await this.devicesService.updateDevicePermission(
      id,
      userId,
      friendId,
      {
        canSeek,
        canTogglePlayPause,
        canModifyMusic,
      },
    );
  }

  @Get('device-permission/:id')
  @ApiOkResponse({ type: DeviceshipResponseDto })
  async getDevicePermission(
    @CurrentUser() userId: number,
    @Param('id') deviceId: string,
  ) {
    return await this.devicesService.deviceship(deviceId, userId);
  }

  @ApiBody({ type: UpdateNameDto })
  @ApiOkResponse({ type: DeviceResponseDto })
  @Patch('update_name')
  async updateDeviceName(
    @CurrentUser() userId: number,
    @Body() updateNameDto: UpdateNameDto,
  ) {
    const { id, name } = updateNameDto;

    return await this.devicesService.updateDeviceName(id, userId, name);
  }

  @ApiOkResponse({ type: [AvailableDeviceResponseDto] })
  @Get('available')
  async getAvailableDevices(@CurrentUser() userId: number) {
    return await this.devicesService.getAvailableDevices(userId);
  }

  @ApiBody({ type: DeviceRequestDto })
  @ApiOkResponse({ type: DeviceResponseDto })
  @Delete('remove-device')
  async deleteDevice(
    @CurrentUser() userId: number,
    @Body() data: DeviceRequestDto,
  ) {
    return await this.devicesService.deleteDevice(userId, data);
  }

  @ApiOkResponse({ type: [DeviceResponseDto] })
  @Get('user_devices')
  async getUserDevices(@CurrentUser() userId: number) {
    return await this.devicesService.getUserDevices(userId);
  }

  @ApiOkResponse({ type: DeviceResponseDto })
  @ApiBody({ type: DeviceRequestDto })
  @Post('add-device')
  async addDevice(
    @CurrentUser() useriId: number,
    @Body() data: DeviceRequestDto,
  ) {
    return await this.devicesService.addDevice(
      useriId,
      data.deviceId,
      data.deviceName,
    );
  }
}
