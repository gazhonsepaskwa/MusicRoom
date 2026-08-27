package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import be.nalebrun.musicroom.repositories.MusicRepository
import be.nalebrun.musicroom.repositories.SocketIORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository,
    val musicRepository: MusicRepository,
    val socketIORepository: SocketIORepository,
    val settingsRepository: ISettingsRepository
) : ViewModel() {
    private val _title = MutableStateFlow<String>("")
    private val _friends = MutableStateFlow<Int>(0)
    private val _isPublic = MutableStateFlow<Boolean>(false)
    private val _isDefault = MutableStateFlow<Boolean>(true)
    private val _musics = MutableStateFlow<List<MusicJson>>(emptyList())
    private val _id = MutableStateFlow<Int>(0)
    private val _version = MutableStateFlow<Int>(0)

    val title: StateFlow<String> = _title
    val friends: StateFlow<Int> = _friends
    val isPublic: StateFlow<Boolean> = _isPublic
    val isDefault: StateFlow<Boolean> = _isDefault
    val musics: StateFlow<List<MusicJson>> = _musics
    val id: StateFlow<Int> = _id
    val version: StateFlow<Int> = _version

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
                            _friends.value = res.playlistships.size
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
                                _friends.value = 0
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
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun removeSongFromPlaylist(musicId: Int, playlistId: Int) { viewModelScope.launch {
        val body = """{"musicId": $musicId, "playlistId": $playlistId, "version": ${version.value}}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull().let { jwt ->
            apiRepository.delete(
                url = "/playlists/remove-music",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        _musics.value = _musics.value.filter { it.id != musicId }
                    }
                    else {
                        Log.d("REMOVE PLAY", response.body?.string() ?: "")
                    }
                },
                onFailure = { _, e -> Log.d("REMOVE PLAY", "didnt work") }
            )
        }
    }
    }

    fun joinPlaylist(playlistId: Int) {
        socketIORepository.on("join_playlist") { args ->
            val data = args.getOrNull(0)
            if (data is JSONObject) {

                // update the playlist version
                Log.d("PLAYLIST", "playlist version: ${data.optInt("version")}")
                _version.value = data.optInt("version")
            }
        }
        // If the playlist change between the api call and the join playlist response on the WS
        socketIORepository.on("playlist_content") { args ->
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                viewModelScope.launch {
                    val playlistString = data.optString("playList")
                    val playlist = Json.decodeFromString<PlaylistJson>(playlistString)
                    _musics.value = playlist.musics.map { it.music }
                }
            }
        }
        socketIORepository.on("music_moved") { args ->
            Log.d("PLAYLIST", "<<< RECEIVED EVENT: 'music_moved' | DATA: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                viewModelScope.launch {
                    val senderId = data.optString("deviceId")
                    val myId = settingsRepository.deviceUuidFlow.firstOrNull()

                    // only apply if the update is not from myself
                    if (senderId != myId) {
                        val oldIndex = data.optInt("oldIndex")
                        val newIndex = data.optInt("newIndex")

                        moveMusic(oldIndex, newIndex)
                    } else {
                        Log.d("PLAYLIST", "Ignored move echo")
                    }
                    // update the playlist version anyway
                    Log.d("PLAYLIST", "playlist version: ${data.optInt("version")}")
                    _version.value = data.optInt("version")
                }
            }
        }
        
        socketIORepository.on("add_music") { args ->
            Log.d("PLAYLIST", "<<< RECEIVED EVENT: 'add_music' | DATA: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                viewModelScope.launch {
                    val musicId = data.optInt("songId")
                    val version = data.optInt("version")

                    //add the music in the list
                    credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                        apiRepository.get(
                            url = "music/$musicId",
                            auth = "Bearer $jwt",
                            onResponse = { _, response ->
                                if (response.code in 200..<300) {
                                    try {
                                        val music = Json.decodeFromString<MusicJson>(response.body?.string() ?: "")
                                        _musics.value = _musics.value + music
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            },
                            onFailure = { _, e -> e.printStackTrace() }
                        )
                    }

                    // update the playlist version
                    Log.d("PLAYLIST", "playlist version: $version")
                    _version.value = version
                }
            }
        }


        socketIORepository.on("remove_music") { args ->
            Log.d("PLAYLIST", "<<< RECEIVED EVENT: 'remove_music' | DATA: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                viewModelScope.launch {
                    val musicId = data.optInt("songId")
                    val version = data.optInt("version")

                    // remove the music from the list
                    _musics.value = _musics.value.filter { it.id != musicId }

                    // update the playlist version
                    Log.d("PLAYLIST", "playlist version: $version")
                    _version.value = version
                }
            }
        }
        
        socketIORepository.emit("join_playlist", playlistId)
    }

    fun leavePlaylist(playlistId: Int) {
        socketIORepository.emit("leave_playlist", playlistId)
        socketIORepository.off("join_playlist")
        socketIORepository.off("playlist_content")
        socketIORepository.off("music_moved")
        socketIORepository.off("add_music")
        socketIORepository.off("remove_music")
    }

    /**
     * Moves the music at the given index to the new index in the playlist
     */
    fun moveMusic(from: Int, to: Int) {
        val currentList = _musics.value.toMutableList()
        if (from in currentList.indices && to in currentList.indices) {
            val item = currentList.removeAt(from)
            currentList.add(to, item)
            _musics.value = currentList
        }
    }

    fun broadcastMove(from: Int, to: Int) {
        viewModelScope.launch {
            val data = JSONObject().apply {
                put("playlistId", _id.value)
                put("oldIndex", from)
                put("newIndex", to)
                put("version", _version.value)
                put("deviceId", settingsRepository.deviceUuidFlow.firstOrNull())
            }
            socketIORepository.emit("move_music", data)
        }
    }

}
