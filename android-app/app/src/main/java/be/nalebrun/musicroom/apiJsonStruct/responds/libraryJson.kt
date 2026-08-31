package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class libraryJson(
    val id: Int,
    val title: String,
    val songs: Int,
    val duration: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class AllPlaylistsJson(
    val id: Int,
    val ownedPlaylists: List<libraryJson>,
    val invitedPlaylists: List<libraryJson>,
)