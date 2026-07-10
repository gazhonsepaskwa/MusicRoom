import { ApiProperty } from '@nestjs/swagger';

export class FriendshipResponseDto {
  @ApiProperty({ example: 'Friend Request Send!' })
  message!: string;
}
