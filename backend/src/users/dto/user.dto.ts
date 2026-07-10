import { ApiProperty } from '@nestjs/swagger';
import { PlaylistListItemDto } from '../../playlists/dto/playlists.dto';

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
