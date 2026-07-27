package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

enum class FriendRequestStatus {
    PENDING,
    ACCEPTED,
    NOTVIEWED,
    REJECTED
}

@Serializable
data class apiFriendJson(
    val otherId       : Int,
    val otherUsername : String,
    val status        : FriendRequestStatus,
    val createdAt     : String
)