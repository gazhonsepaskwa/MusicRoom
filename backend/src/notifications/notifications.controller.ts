import { Controller, Get, Patch } from '@nestjs/common';
import { NotificationsService } from './notifications.service';
import { CurrentUser } from '../common/decorators/current-user.decorator';

@Controller('notifications')
export class NotificationsController {
  constructor(
	private readonly notificationsService: NotificationsService,
	) {}

  @Get('pending-notifications')
  async getPendingNotifications(@CurrentUser() userId: number) {
	return await this.notificationsService.getPendingNotifications(userId);
  }

  @Patch('update-notifications')
  async updateNotifications(@CurrentUser() userId:number){
	await this.notificationsService.updateUsersNotification(userId);
  }
}
