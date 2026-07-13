package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.AlbumsJson
import be.nalebrun.musicroom.apiJsonStruct.responds.ArtistAlbumsJson
import be.nalebrun.musicroom.apiJsonStruct.responds.ArtistSongsJson
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
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
    val credentialRepository: ICredentialRepository
) : ViewModel() {
    private val _musics = MutableStateFlow<List<MusicJson>?>(null)
    private val _albums = MutableStateFlow<List<AlbumsJson>?>(null)
    private val _artist = MutableStateFlow<String?>("")
    private val _artistImage = MutableStateFlow<String?>("")

    val artist: StateFlow<String?> = _artist
    val musics: StateFlow<List<MusicJson>?> = _musics
    val albums: StateFlow<List<AlbumsJson>?> = _albums
    val artistImage: StateFlow<String?> = _artistImage

    fun getAlbumsFromArtist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "https://musicroom.nalebrun.be/artist/$id",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
//                    response.body?.string()?.let { Log.d("ALBUMS",it) }
                    val res = Json.decodeFromString<ArtistAlbumsJson>(response.body?.string() ?: "")

                    _artist.value = res.title
                    _albums.value = res.albums
                    _artistImage.value = res.images[1]
                },
                onFailure = { _, _ -> }
            )

        }
    }}

    fun getSongsFromArtist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "https://musicroom.nalebrun.be/artist/musics/$id",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    val res = Json.decodeFromString<ArtistSongsJson>(response.body?.string() ?: "")
                    _artist.value = res.title
                    _musics.value = res.musics
                    _artistImage.value = res.images[2]
                },
                onFailure = { _, _ -> }
            )

        }
    }}
}
