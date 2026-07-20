import { IsString, MinLength, MaxLength, IsAlphanumeric, IsEmail, Matches } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class EmailDto {
	@ApiProperty({})
	@IsEmail()
	email!: string
}