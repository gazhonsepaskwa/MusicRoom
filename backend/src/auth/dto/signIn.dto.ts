import { IsNotEmpty, IsString } from "class-validator";
import { ApiProperty } from '@nestjs/swagger';


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
