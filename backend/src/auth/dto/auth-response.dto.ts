import { ApiProperty } from '@nestjs/swagger';

export class AuthTokenResponseDto {
  @ApiProperty({ example: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...' })
  access_token!: string;
}

export class AuthMessageResponseDto {
  @ApiProperty({ example: 'Please Check your mailbox for the verification email we have send you (you have 1 hour)' })
  message!: string;
}

export class UserProfileResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'john_doe' })
  username!: string;

  @ApiProperty({ example: 'john_doe@gmail.com' })
  email!: string;
}
