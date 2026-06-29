import { BadRequestException, Controller, Get, Req } from '@nestjs/common';
import { NotificationsService } from './notifications.service';
import { AuthGuard } from '../auth/auth.guard';

@Controller('notifications')
export class NotificationsController {
  constructor(
	private readonly notificationsService: NotificationsService,
	private readonly authGuard: AuthGuard,
	) {}

  @Get('pending-notifications')
  async getPendingNotifications(@Req() request) {
	const userId = this.authGuard.getUserIdFromRequest(request);
	if (userId === null)
		throw new BadRequestException("Invalid JWT token: no user found");
	return await this.notificationsService.getPendingNotifications(userId);
  }
}
