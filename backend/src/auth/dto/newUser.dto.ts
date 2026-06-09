import { IsString, MinLength, MaxLength, IsAlphanumeric, IsEmail, Matches } from 'class-validator';

export class NewUserDto {
	@IsString()
	@MinLength(3)
	@MaxLength(20)
	username!: string;

	@IsString()
	@MinLength(8)
	@MaxLength(30)
	@Matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&,.°§+])/,
		{
			message: "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character. (And a sacrifice to the coding gods)"
		})
	password!: string;

	@IsString()
	@IsEmail()
	email!: string;
}