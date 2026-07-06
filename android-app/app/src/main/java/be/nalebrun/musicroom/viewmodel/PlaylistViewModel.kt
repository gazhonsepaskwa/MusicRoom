package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistMusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.playlistJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {
    private val _title = MutableStateFlow<String?>(null)
    private val _friends = MutableStateFlow<Int?>(0)
    private val _isPublic = MutableStateFlow<Boolean?>(false)
    private val _musics = MutableStateFlow<List<PlaylistMusicJson>?>(null)

    val title: StateFlow<String?> = _title
    val friends: StateFlow<Int?> = _friends
    val isPublic: StateFlow<Boolean?> = _isPublic
    val musics: StateFlow<List<PlaylistMusicJson>?> = _musics

    fun getPlaylist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "https://musicroom.nalebrun.be/playlists/get/$id", //+ id
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    val res = Json.decodeFromString<playlistJson>(response.body?.string() ?: "")
                    _title.value = res.title
                    _isPublic.value = res.isPublic
                    _musics.value = res.musics
                             },
                onFailure = { _, _ -> }
            )
        }
    }}

    fun updatePublicState(id: Int) { viewModelScope.launch {
        val body = FormBody.Builder()
            .add("isPublic", _isPublic.toString())
            .build()
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.patch(
                url = "https://musicroom.nalebrun.be/playlists/update/$id",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response -> Log.i("STATUS", response.body.toString()) },
                onFailure = { _, e -> e.message?.let { Log.i("STATUS 2", it) } }
            )
        }
    }}
}
