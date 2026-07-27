package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

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
