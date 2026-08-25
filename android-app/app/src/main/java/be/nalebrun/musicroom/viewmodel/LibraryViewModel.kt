package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.AllPlaylistsJson
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.libraryJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<libraryJson>>(emptyList())
    private val _sharedPlaylists = MutableStateFlow<List<libraryJson>>(emptyList())

    val playlists: StateFlow<List<libraryJson>> = _playlists
    val sharedPlaylists: StateFlow<List<libraryJson>> = _sharedPlaylists

    init {
        getPlaylists()
    }
    fun getPlaylists() { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
            url = "/users/profile",
            auth = "Bearer $jwt",
            onResponse = { _, response ->
                if (response.code in 200..<300) {
                    try {
                        val res =
                            Json.decodeFromString<AllPlaylistsJson>(response.body?.string() ?: "")
                        _playlists.value = res.ownedPlaylists
                        _sharedPlaylists.value = res.invitedPlaylists
                        Log.d("GetPlaylist Library", "Get playlists success")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            onFailure = { _, e -> e.printStackTrace()}
        )
    }
    }}

    fun createPlaylist(title: String, status: Boolean, callback: () -> Unit) { viewModelScope.launch {
        val body = """{"title": "$title", "isPublic": $status, "status":"status"}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.post(
                url = "/playlists/create",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        Log.d("Library", "Playlist was created")
                        callback()
                    } else {
                        Log.d("Library", "Create playlist error: ${response.body?.string()}")
                    }
                },
                onFailure = { _, e -> e.printStackTrace() }
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

    fun deletePlaylist(id: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.delete(
                url = "/playlists/delete/$id",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        _playlists.value = _playlists.value.filter { it.id != id }
                    }
                },
                onFailure = { _, e -> e.printStackTrace()}
            )
        }
    }}

    fun renamePlaylist(id: Int, title: String) { viewModelScope.launch {
        val body = """{"title": "$title"}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.patch(
                url = "/playlists/update/$id",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        val newPlaylists = _playlists.value.toMutableList()
                        var i = 0
                        for (playlist in newPlaylists) {
                            if (playlist.id == id) {
                                val newPlaylist = libraryJson(
                                    id = playlist.id,
                                    title = title,
                                    songs = playlist.songs,
                                    duration = playlist.duration
                                )
                                newPlaylists[i] = newPlaylist
                                break
                            }
                            i++
                        }
                        _playlists.value = newPlaylists
                    }
                },
                onFailure = { _, e -> e.printStackTrace()}
            )
        }
    }}
}