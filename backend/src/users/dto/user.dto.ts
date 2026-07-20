import { ApiProperty } from '@nestjs/swagger';
import { PlaylistListItemDto } from '../../playlists/dto/playlists.dto';
import { visibilityStatus } from '../../../generated/prisma/client';
import { IsString, MinLength, MaxLength, IsAlphanumeric, IsEmail, Matches } from 'class-validator';



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
  
  @ApiProperty({ example: true })
  isFriend!: boolean

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

export class UserUpdateDto {
	@ApiProperty({
		example: 'john_doe',
		minLength: 3,
		maxLength: 20,
	})
	@IsString()
	@MinLength(3)
	@MaxLength(20)
	username?: string

	@IsString()
	@MinLength(8)
	@MaxLength(30)
	@Matches(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&,.°§+])/,
		{
			message: "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character. (And a sacrifice to the coding gods)"
		})
	password?: string

	@ApiProperty({example: 1})
	firstPreferredMusicId?: number
	@ApiProperty({example: 2})
	secondPreferredMusicId?: number
	@ApiProperty({example: 3})
	thirdPreferredMusicId?: number
	@ApiProperty({
    	enum: visibilityStatus,
    	example: visibilityStatus.PRIVATE,
    	description: 'The new visibility status.',
  	})
	showAddress?: visibilityStatus
	@ApiProperty({
    	enum: visibilityStatus,
    	example: visibilityStatus.PRIVATE,
    	description: 'The new visibility status.',
  	})
	showCreatedPlaylist?: visibilityStatus
	@ApiProperty({
    	enum: visibilityStatus,
    	example: visibilityStatus.PRIVATE,
    	description: 'The new visibility status.',
  	})
	showFriends?: visibilityStatus
	@ApiProperty({
    	enum: visibilityStatus,
    	example: visibilityStatus.PRIVATE,
    	description: 'The new visibility status.',
  	})
	showInvitedPlaylist?: visibilityStatus

	@ApiProperty({
    	enum: visibilityStatus,
    	example: visibilityStatus.PRIVATE,
    	description: 'The new visibility status.',
  	})
	showPreferedMusics: visibilityStatus

}