package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MusicJson(
    val id:       Int,
    val title:    String = "",
    val duration: Long = 0,
    val album:    AlbumJson? = null,
    val artists:  List<ArtistJson> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class AlbumJson(
    val title: String,
    val date: String? = null,
    val images: List<String> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ArtistJson(
    val title: String
)
