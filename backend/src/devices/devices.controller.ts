import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
} from '@nestjs/common';
import { DevicesService } from './devices.service';
import { UpdatePermissionDto } from './dto/updatePermission.dto';
import { UpdateNameDto } from './dto/updateName.dto';
import { CurrentUser } from '../decorator/current-user.decorator';

@Controller('devices')
export class DevicesController {
  constructor(private readonly devicesService: DevicesService) {}

  @Patch('update_permissions')
  updateDevicesPerm(
    @CurrentUser() userId: number,
    @Body() updatePermissionDto: UpdatePermissionDto,
  ) {
    const { id, friendId, canSeek, canTogglePlayPause, canModifyMusic } =
      updatePermissionDto;

    console.log(
      'DevicesController.updateDevicesPerm called with id:',
      id,
      'friendId:',
      friendId,
      'canSeek:',
      canSeek,
      'canTogglePlayPause:',
      canTogglePlayPause,
      'canModifyMusic:',
      canModifyMusic,
    );
    return this.devicesService.updateDevicePermission(id, userId, friendId, {
      canSeek,
      canTogglePlayPause,
      canModifyMusic,
    });
  }

  @Patch('update_name')
  updateDeviceName(
    @CurrentUser() userId: number,
    @Body() updateNameDto: UpdateNameDto,
  ) {
    const { id, name } = updateNameDto;
    console.log(
      'DevicesController.updateDeviceName called with id:',
      id,
      'name:',
      name,
    );
    return this.devicesService.updateDeviceName(id, userId, name);
  }

  @Get('available')
  getAvailableDevices(@CurrentUser() userId: number) {
    console.log(
      'DevicesController.getAvailableDevices called with id:',
      userId,
    );
    return this.devicesService.getAvailableDevices(userId);
  }

  @Delete(':id')
  deleteDevice(@CurrentUser() userId: number, @Param('id') id: string) {
    return this.devicesService.deleteDevice(id, userId);
  }

  @Get('user_devices')
  getUserDevices(@CurrentUser() userId: number) {
    console.log('DevicesController.getUserDevices called with id:', userId);
    return this.devicesService.getUserDevices(userId);
  }
}
