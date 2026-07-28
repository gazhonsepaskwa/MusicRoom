package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class ForeignDevice (
    val deviceId: String,
    val name: String,
    val userId: Int,
    val canSeek: Boolean,
    val canTogglePlayPause: Boolean,
    val canModifyMusic: Boolean,
    val createdAt: String,
    val isOnlineDevice: Boolean
)