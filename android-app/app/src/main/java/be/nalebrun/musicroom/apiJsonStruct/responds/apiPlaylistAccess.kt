package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PlaylistAccessJson(
    val addresseeId: Int,
    val addresseeName: String,
    val playlistId: Int,
    val playlistName: String,
    val status: String,
)