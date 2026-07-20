import { IsString } from "class-validator";
import { ApiProperty } from '@nestjs/swagger';


export class SignInDto {
  @ApiProperty({example: ['john doe', 'john_doe@gmail.com']})
  @IsString()
  username!: string;
  
  @ApiProperty({})
  @IsString()
  password!: string;
  
  @ApiProperty({})
  @IsString()
  deviceID!: string;
  
  @ApiProperty({})
  @IsString()
  deviceName!: string;
}
