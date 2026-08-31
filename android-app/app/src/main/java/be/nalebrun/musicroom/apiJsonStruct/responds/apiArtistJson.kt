package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable


@Serializable
data class AlbumsArtistJson(
    val id: Int,
    val title: String,
    val date: String,
    val images: List<String>
)

@Serializable
data class ArtistSongsJson(
    val id: Int,
    val title: String,
    val images: List<String>,
    val musics: List<MusicJson>,
    val type: String
)

@Serializable
data class ArtistAlbumsJson(
    val id: Int,
    val title: String,
    val images: List<String>,
    val albums: List<AlbumsArtistJson>,
    val type: String

)
