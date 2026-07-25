package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicSongsAlbumJson
import be.nalebrun.musicroom.apiJsonStruct.responds.SingleAlbumJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {
    private val _musics = MutableStateFlow<List<MusicSongsAlbumJson>>(emptyList())
    private val _albumName = MutableStateFlow<String>("")
    private val _image = MutableStateFlow<String>("")

    val musics : StateFlow<List<MusicSongsAlbumJson>> = _musics
    val albumName: StateFlow<String> = _albumName
    val image: StateFlow<String> = _image

    fun getAlbum(albumId: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            if (jwt.isNotEmpty()) {
                apiRepository.get(
                    "https://musicroom.nalebrun.be/album/$albumId",
                    "Bearer $jwt",
                    {_, response ->
                        if (response.code in 200 ..<300) {
                            val body = response.body?.string()
                            if (body != null) {
                                try {
                                    val res = Json.decodeFromString<SingleAlbumJson>(body)
                                    _musics.value = res.music
                                    _albumName.value = res.title
                                    if (res.images.size > 2)
                                        _image.value = res.images[1]
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        Log.d("EXIT ALB", "???")}
                    },
                    {_, e -> e.printStackTrace() }
                )
            }
        }
    }

    }
}