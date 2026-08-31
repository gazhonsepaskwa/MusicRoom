package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestActionJson(
    val senderId: Int,
    val answer: String
)
