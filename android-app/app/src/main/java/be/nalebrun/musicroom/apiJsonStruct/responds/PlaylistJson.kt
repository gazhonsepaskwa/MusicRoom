package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
class PlaylistMusicJson(
    val index: Int,
    val music: MusicJson,
)
@Serializable
class PlaylistJson(
    val id: Int,
    val title: String,
    val isPublic: Boolean,
    val isDefault: Boolean,
    val status: String,
    val type: String,
    val musics: List<PlaylistMusicJson>,
    val playlistships: List<String>,
) {
}