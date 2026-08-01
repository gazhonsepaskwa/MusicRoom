package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PlaylistNotificationJson (
    val type: String,
    val createdAt: String,
    val status: FriendRequestStatus,
    val requesterId: Int? = null,
    val requesterName: String? = null,
    val playlistId: Int? = null,
    val playlistName: String? = null
        )

@Serializable
data class NotificationJson(
    val type: String,
    val createdAt: String,
    val status: FriendRequestStatus,
    val requesterId: Int? = null,
    val requesterName: String? = null,
    val playlistId: Int? = null,
    val playlistName: String? = null
)
