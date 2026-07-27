package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class UserProfileJson(
    val id: Int,
    val username: String,
    val email: String? = null,
    val friends: Int? = 0,
    val playlists: Int = 0,
    val invitedPlaylistsNbr: Int = 0,
    val ownedPlaylistsNbr: Int = 0,
    val isFriend: FriendRequestStatus? = null,
    val firstPreferedMusicId: Int? = null,
    val secondPreferedMusicId: Int? = null,
    val thirdPreferedMusicId: Int? = null,
    val ownedPlaylists: List<PlaylistJson>? = emptyList(),
    val invitedPlaylists: List<PlaylistJson>? = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PlaylistJson(
    val id: Int,
    val title: String,
    val duration: Long = 0,
    val musicCount: Int = 0,
    val images: List<String> = emptyList()
)
