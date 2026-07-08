import { ApiProperty } from '@nestjs/swagger';

export class UserResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'john_doe' })
  username!: string;

  @ApiProperty({ example: 'john_doe@gmail.com' })
  email!: string;
}
