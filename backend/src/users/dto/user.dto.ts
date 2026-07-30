import { ApiProperty, ApiPropertyOptional, PartialType } from '@nestjs/swagger';
import { PlaylistListItemDto } from '../../playlists/dto/playlists.dto';
import { visibilityStatus, invitationStatus } from '../../../generated/prisma/client';
import { IsString, MinLength, MaxLength, Matches, IsEnum, IsOptional, IsInt, IsBoolean, IsNotEmpty } from 'class-validator';


export class UserResponseDto {
  @ApiProperty({ example: 1 })
  id!: number;

  @ApiProperty({ example: 'Chuck Yesris' })
  username!: string;

  @ApiProperty({ example: 'Chuck_Yesris@gmail.com' })
  email!: string;
}

export class UserProfileResponseDto {
  @ApiProperty({ example: 1 })
  id!: number

  @ApiProperty({ example: 'Chuck Yesris' })
  username!: string

  @ApiProperty({ example: 'Chuck_Yesris@gmail.com' })
  email!: string | null

  @ApiProperty({ example: 10 })
  friends!: number | null

  @ApiProperty({ example: 5 })
  playlists!: number

  @ApiProperty({ example: 3 })
  invitedPlaylistsNbr!: number

  @ApiProperty({ example: 2 })
  ownedPlaylistsNbr!: number
  
  @ApiProperty({ 
	enum: invitationStatus,
	example: 'ACCEPTED' })
  isFriend!: invitationStatus | null

  @ApiProperty({ example: 12 })
  firstPreferedMusicId!: number | null

  @ApiProperty({ example: 15 })
  secondPreferedMusicId!: number | null

  @ApiProperty({ example: 20 })
  thirdPreferedMusicId!: number | null

  @ApiProperty({ type: [PlaylistListItemDto] })
  ownedPlaylists!: PlaylistListItemDto[] | null

  @ApiProperty({ type: [PlaylistListItemDto] })
  invitedPlaylists!: PlaylistListItemDto[] | null
}

export class UserProfileDto {
	@ApiPropertyOptional({
		example: 'john_doe',
		minLength: 3,
		maxLength: 20,
	})
	@IsOptional()
	@IsString()
	@MinLength(3)
	@MaxLength(20)
	username?: string

	@ApiPropertyOptional({example: 1})
	@IsOptional()
	@IsInt()
	firstPreferredMusicId?: number

	@ApiPropertyOptional({example: 2})
	@IsOptional()
	@IsInt()
	secondPreferredMusicId?: number

	@ApiPropertyOptional({example: 3})
	@IsOptional()
	@IsInt()
	thirdPreferredMusicId?: number

	@ApiPropertyOptional({
		enum: visibilityStatus,
		example: visibilityStatus.PRIVATE,
		description: 'The new visibility status.',
	})
	@IsOptional()
	@IsEnum(visibilityStatus)
	showAddress?: visibilityStatus

	@ApiPropertyOptional({
		enum: visibilityStatus,
		example: visibilityStatus.PRIVATE,
		description: 'The new visibility status.',
	})
	@IsOptional()
	@IsEnum(visibilityStatus)
	showCreatedPlaylist?: visibilityStatus

	@ApiPropertyOptional({
		enum: visibilityStatus,
		example: visibilityStatus.PRIVATE,
		description: 'The new visibility status.',
	})
	@IsOptional()
	@IsEnum(visibilityStatus)
	showFriends?: visibilityStatus

	@ApiPropertyOptional({
		enum: visibilityStatus,
		example: visibilityStatus.PRIVATE,
		description: 'The new visibility status.',
	})
	@IsOptional()
	@IsEnum(visibilityStatus)
	showInvitedPlaylist?: visibilityStatus

	@ApiPropertyOptional({
		enum: visibilityStatus,
		example: visibilityStatus.PRIVATE,
		description: 'The new visibility status.',
	})
	@IsOptional()
	@IsEnum(visibilityStatus)
	showPreferedMusics?: visibilityStatus
}

export class UserUpdateDto extends PartialType(UserProfileDto) {}

export class ChangePasswordDto {

	@ApiProperty({})
	@IsOptional()
	@IsString()
	oldPassword?: string

	@ApiProperty({})
	@IsString()
	@IsNotEmpty()
	@MinLength(8)
	@MaxLength(30)
	@Matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&,.°§+])/,
		{
			message: "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character. (And a sacrifice to the coding gods)"
		})
	newPassword!: string
}

export class PasswordCheckDto {
	@ApiProperty({})
	@IsBoolean()
	@IsNotEmpty()
	isPasswordSet!: boolean
}
