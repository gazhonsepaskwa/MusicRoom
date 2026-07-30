import {
  IsString,
  MinLength,
  MaxLength,
  IsAlphanumeric,
  IsEmail,
  Matches,
  IsNotEmpty,
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';


export class NewUserDto {
	@ApiProperty({
		example: 'john_doe',
		minLength: 3,
		maxLength: 20,
	})
	@IsString()
	@IsNotEmpty()
	@MinLength(3)
	@MaxLength(20)
	username!: string;

	@ApiProperty({
		example: '********',
		minLength: 8,
		maxLength: 30,
	})
	@IsString()
	@IsNotEmpty()
	@MinLength(8)
	@MaxLength(30)
	@Matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&,.°§+])/,
		{
			message: "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character. (And a sacrifice to the coding gods)"
		})
	password!: string;

	@ApiProperty({
		example: 'john_doe@gmail.com',
	})
	@IsString()
	@IsNotEmpty()
	@IsEmail()
	email!: string;

	@ApiProperty({
	})
	@IsString()
	@IsNotEmpty()
	deviceID!: string;
	
	@ApiProperty({
		example: 'john_doe\'s 3310',
	})
	@IsString()
	@IsNotEmpty()
	deviceName!: string;
}

export class DeleteAccountDto {
	@ApiProperty({
		example: '********',
	})
	@IsString()
	@IsNotEmpty()
	password!: string;
}