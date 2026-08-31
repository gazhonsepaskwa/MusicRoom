package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
class Playlistship(
)
@Serializable
class PlaylistMusicJson(
    val index: Int,
    val music: MusicJson,
)
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
class PlaylistJson(
    val id: Int,
    val title: String,
    val isPublic: Boolean,
    val isDefault: Boolean,
    val status: String,
    val type: String,
    val musics: List<PlaylistMusicJson>,
    val playlistships: List<Playlistship>,
) {
}