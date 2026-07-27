import { Controller, Get, Patch } from '@nestjs/common';
import { ApiOkResponse } from '@nestjs/swagger';
import { NotificationsService } from './notifications.service';
import { CurrentUser } from '../common/decorators/current-user.decorator';
import { NotificationResponseDto } from './dto/notification-response.dto';

@Controller('notifications')
export class NotificationsController {
  constructor(
	private readonly notificationsService: NotificationsService,
	) {}

  @ApiOkResponse({ type: [NotificationResponseDto] })
  @Get('pending-notifications')
  async getPendingNotifications(@CurrentUser() userId: number) {
	return await this.notificationsService.getPendingNotifications(userId);
  }

  @ApiOkResponse({ description: 'Notifications marked as viewed.' })
  @Patch('update-notifications')
  async updateNotifications(@CurrentUser() userId:number){
	await this.notificationsService.updateUsersNotification(userId);
  }
}
