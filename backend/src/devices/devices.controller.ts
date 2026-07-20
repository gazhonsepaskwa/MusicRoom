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
  updateDevicesPerm(
    @CurrentUser() userId: number,
    @Body() updatePermissionDto: UpdatePermissionDto,
  ) {
    const { id, friendId, canSeek, canTogglePlayPause, canModifyMusic } =
      updatePermissionDto;

    return this.devicesService.updateDevicePermission(id, userId, friendId, {
      canSeek,
      canTogglePlayPause,
      canModifyMusic,
    });
  }

  @ApiBody({ type: UpdateNameDto })
  @ApiOkResponse({ type: DeviceResponseDto })
  @Patch('update_name')
  updateDeviceName(
    @CurrentUser() userId: number,
    @Body() updateNameDto: UpdateNameDto,
  ) {
    const { id, name } = updateNameDto;

    return this.devicesService.updateDeviceName(id, userId, name);
  }

  @ApiOkResponse({ type: [AvailableDeviceResponseDto] })
  @Get('available')
  getAvailableDevices(@CurrentUser() userId: number) {
    return this.devicesService.getAvailableDevices(userId);
  }

  @ApiParam({ name: 'id', type: String })
  @ApiOkResponse({ type: DeviceResponseDto })
  @Delete(':id')
  deleteDevice(@CurrentUser() userId: number, @Param('id') id: string) {
    return this.devicesService.deleteDevice(id, userId);
  }

  @ApiOkResponse({ type: [DeviceResponseDto] })
  @Get('user_devices')
  getUserDevices(@CurrentUser() userId: number) {
    return this.devicesService.getUserDevices(userId);
  }
}
