package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.AlbumsArtistJson
import be.nalebrun.musicroom.apiJsonStruct.responds.ArtistAlbumsJson
import be.nalebrun.musicroom.apiJsonStruct.responds.ArtistSongsJson
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository,
    val musicRepository: MusicRepository
) : ViewModel() {
    private val _musics = MutableStateFlow<List<MusicJson>>(emptyList())
    private val _albums = MutableStateFlow<List<AlbumsArtistJson>>(emptyList())
    private val _artist = MutableStateFlow<String?>("")
    private val _artistImage = MutableStateFlow<String?>("")

    val artist: StateFlow<String?> = _artist
    val musics: StateFlow<List<MusicJson>> = _musics
    val albums: StateFlow<List<AlbumsArtistJson>> = _albums
    val artistImage: StateFlow<String?> = _artistImage

    fun getAlbumsFromArtist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "/artist/$id",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200 ..<300) {
                        val body = response.body?.string()
                        if (body != null) {
                            try {
                                val res = Json.decodeFromString<ArtistAlbumsJson>(body)

                                _artist.value = res.title
                                _albums.value = res.albums
                                if (res.images.isNotEmpty())
                                    _artistImage.value = res.images[1]
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }}
                },
                onFailure = { _, e -> e.printStackTrace() }
            )

        }
    }}

    fun getSongsFromArtist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "/artist/musics/$id",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    val res = Json.decodeFromString<ArtistSongsJson>(response.body?.string() ?: "")
                    _artist.value = res.title
                    _musics.value = res.musics
                    if (res.images.size >= 3)
                        _artistImage.value = res.images[2]
                },
                onFailure = { _, _ -> }
            )

        }
    }}
}
