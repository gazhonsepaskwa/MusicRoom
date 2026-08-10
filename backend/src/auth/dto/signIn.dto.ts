import {
  IsString,
  MinLength,
  MaxLength,
  Matches,
  IsNotEmpty,
} from 'class-validator';import { ApiProperty } from '@nestjs/swagger';


export class SignInDto {
  @ApiProperty({example: ['john doe', 'john_doe@gmail.com']})
  @IsString()
  @IsNotEmpty()
  username!: string;
  
  @ApiProperty({})
  @IsString()
  @IsNotEmpty()
  password!: string;
  
  @ApiProperty({})
  @IsString()
  @IsNotEmpty()
  deviceID!: string;
  
  @ApiProperty({})
  @IsString()
  @IsNotEmpty()
  deviceName!: string;
}

export class ResetPasswordDto {
  @ApiProperty({})
  @IsString()
  @IsNotEmpty()
  email!: string;

  @IsString()
  @IsNotEmpty()
  @MinLength(8)
  @MaxLength(30)
  @Matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&,.°§+])/,
    {
      message: "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character. (And a sacrifice to the coding gods)"
    })
  password!: string;

  @ApiProperty({})
  @IsString()
  @IsNotEmpty()
  token!: string;
}