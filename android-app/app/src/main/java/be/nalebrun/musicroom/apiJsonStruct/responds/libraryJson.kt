package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class libraryJson(
    val id: Int,
    val title: String,
    val songs: Int,
    val duration: Int
)
@Serializable
data class libraryPlayistsJson(
    val playlists: List<libraryJson>
)
