package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository,
    val musicRepository: MusicRepository
) : ViewModel() {
    private val _title = MutableStateFlow<String>("")
    private val _friends = MutableStateFlow<Int>(0)
    private val _isPublic = MutableStateFlow<Boolean>(false)
    private val _isDefault = MutableStateFlow<Boolean>(true)
    private val _musics = MutableStateFlow<List<MusicJson>>(emptyList())
    private val _id = MutableStateFlow<Int>(0)

    val title: StateFlow<String> = _title
    val friends: StateFlow<Int> = _friends
    val isPublic: StateFlow<Boolean> = _isPublic
    val isDefault: StateFlow<Boolean> = _isDefault
    val musics: StateFlow<List<MusicJson>> = _musics
    val id: StateFlow<Int> = _id

    fun getPlaylist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "/playlists/get/$id",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200 ..<300) {
                        try {
                            val res =
                                Json.decodeFromString<PlaylistJson>(response.body?.string() ?: "")
                            _title.value = res.title
                            _isPublic.value = res.isPublic
                            val eph = mutableListOf<MusicJson>()
                            for (it in res.musics ){
                                eph.add(MusicJson(
                                    it.music.id,
                                    it.music.title,
                                    it.music.duration,
                                    it.music.album,
                                    it.music.artists))

                            }
                            _id.value = res.id
                            _musics.value = eph
                            _isDefault.value = res.isDefault
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }},
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun getFavorite() {
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull().let { jwt ->
                apiRepository.get(
                    url = "/playlists/favorite",
                    auth = "Bearer $jwt",
                    onResponse = { _, response ->
                        if (response.code in 200..<300) {
                            try {
                                val res = Json.decodeFromString<PlaylistJson>(
                                    response.body?.string() ?: ""
                                )
                                _title.value = res.title
                                _isPublic.value = res.isPublic
                                val eph = mutableListOf<MusicJson>()
                                for (it in res.musics ){
                                    eph.add(MusicJson(
                                        it.music.id,
                                        it.music.title,
                                        it.music.duration,
                                        it.music.album,
                                        it.music.artists))
                                }
                                _musics.value = eph
                                _isDefault.value = res.isDefault
                                _id.value = res.id
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    onFailure = { _, e -> e.printStackTrace()}
                )
            }
        }
    }

    fun updatePublicState(id: Int) { viewModelScope.launch {
        val body = """{"isPublic":${!_isPublic.value}}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.patch(
                url = "/playlists/update/$id",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200 ..<300) {
                        _isPublic.value = !_isPublic.value
                    }
                },
                onFailure = { _, e -> e.message?.let { Log.i("STATUS 2", it) } }
            )
        }
    }}

    fun addMusicToPlaylist(musicId: Int, playlistId: Int) { viewModelScope.launch {
        val body = """{"musicId": $musicId, "playlistId": $playlistId}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.post(
                url = "/playlists/add-music",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
//                    if (response.code in 200..<300) {
//
//                    }
                },
                onFailure = { _, e -> e.printStackTrace()}
            )
        }
    }}

    fun removeSongFromPlaylist(musicId: Int, playlistId: Int) { viewModelScope.launch {
        val body = """{"musicId": $musicId, "playlistId": $playlistId}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.delete(
                url = "/playlists/remove-music",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        _musics.value = _musics.value.filter { it.id != musicId }
                    }
                },
                onFailure = { _, e -> Log.d("REMOVE PLAY", "didnt work") }
            )
        }
    }
    }

}
