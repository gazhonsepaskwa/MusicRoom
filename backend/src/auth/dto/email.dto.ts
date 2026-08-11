import { IsEmail, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class EmailDto {
	@ApiProperty({})
	@IsEmail()
	@IsNotEmpty()
	email!: string
}