package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class libraryJson(
    val id: Int,
    val title: String,
    val songs: Int,
    val duration: Int
)
