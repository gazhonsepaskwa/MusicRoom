package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
sealed class SearchResponseJson {
    abstract val id: Int
    abstract val type: String // discriminator to choose the right class to cast into

    @Serializable
    @SerialName("music") // the discriminator value
    data class Music(
        override val    id:         Int,
        val             title:      String,
        val             duration:   Long,
        val             album:      AlbumJson,
        val             artists:    List<ArtistJson>,
        override val    type:       String = "music"
    ) : SearchResponseJson()

    @Serializable
    @SerialName("album")
    data class Album(
        override val    id:         Int,
        val             title:      String,
        val             date:       String,
        val             images:     List<String>,
        val             music:      List<MusicJson>,
        val             artists:    List<ArtistJson>,
        override val    type:       String = "album"
    ) : SearchResponseJson()

    @Serializable
    @SerialName("artist")
    data class Artist(
        override val    id :        Int,
        val             title:      String,
        val             images:     List<String>,
        val             albums:     List<AlbumJson>,
        override val    type :      String = "artist",

        ) : SearchResponseJson()

    @Serializable
    @SerialName("user")
    data class User(
        override val    id :        Int,
        val             username :  String,
        val             email :     String,
        override val    type:       String = "user"
    ) : SearchResponseJson()
}
