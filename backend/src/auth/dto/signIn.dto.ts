import { IsString } from 'class-validator';

export class SignInDto {
  @IsString()
  username!: string;
  @IsString()
  password!: string;
  @IsString()
  deviceID!: string;
  @IsString()
  deviceName!: string;
}
