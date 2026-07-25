package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
class PlaylistArtistJson(
    val title: String,
)

@Serializable
class PlaylistSongJson(
    val id: Int,
    val title: String,
    val duration: Int,
    val artists: List<PlaylistArtistJson>
)

@Serializable
class PlaylistMusicJson(
    val index: Int,
    val music: PlaylistSongJson,
)
@Serializable
class playlistJson(
    val id: Int,
    val title: String,
    val isPublic: Boolean,
    val isDefault: Boolean,
    val status: String,
    val musics: List<PlaylistMusicJson>,
    val playlistships: List<String>,
    val type: String
) {
}