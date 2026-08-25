package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.repositories.IMusicRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import be.nalebrun.musicroom.repositories.ISocketIORepository
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.repositories.UiMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

data class ConnectionRequest(
    val deviceId: String,
    val userId: Int
)

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val socketIORepository: ISocketIORepository,
    private val musicRepository: IMusicRepository,
    private val settingsRepository: ISettingsRepository,
    private val uiMessageManager: UiMessageManager,
) : ViewModel() {

    private val _incomingRequest = MutableStateFlow<ConnectionRequest?>(null)
    val          incomingRequest = _incomingRequest.asStateFlow()

    // reflect that the user is in a room
    private val _isInRoom = MutableStateFlow(false)
    val          isInRoom = _isInRoom.asStateFlow()

    init {
        // (host) receiving connection request
        socketIORepository.on("hostRequest") { args ->
            val data = args.getOrNull(0)
            Log.d("SocketViewModel", "hostRequest received. Data type: ${data?.javaClass?.simpleName}. Data: $data")
            
            if (data is JSONObject) {
                val deviceId = data.optString("emitDeviceID")
                val userId = data.optInt("emitUserId")
                _incomingRequest.value = ConnectionRequest(deviceId, userId)
            } else {
                Log.w("SocketViewModel", "Received hostRequest but data is not a JSONObject")
            }
        }

        // (controller) receiving answer the host connection
        socketIORepository.on("hostResponse") { args ->
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                viewModelScope.launch {
                    _isInRoom.value = true
                    val musicListJson = data.optJSONArray("musicList")?.toString() ?: "[]"
                    val musicList = try {
                        Json.decodeFromString<List<MusicJson>>(musicListJson)
                    } catch (e: Exception) {
                        Log.e("SocketViewModel", "Error decoding musicList", e)
                        emptyList()
                    }

                    musicRepository.startRemoteControl(
                        newIsPlaying   = data.optString("isPlaying"),
                        newPosition    = data.optLong("currentTime"),
                        newWaitingList = musicList,
                        newCurrentSong = data.optInt("currentSongIndex")
                    )
                    musicRepository.currentSongIndex.value  = data.optInt("currentMusicId")
                    musicRepository.remoteControlHost.value = data.optString("deviceId")
                    uiMessageManager.showMessage("You joined the music room")
                }
            } else {
                Log.w("SocketViewModel", "Received hostResponse but data is not a JSONObject")
            }
        }

        socketIORepository.on("playback_state") { args ->
            Log.d("SocketIORepository", ">>> [playback_state] INCOMING: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                viewModelScope.launch {
                    if (data.has("isPlaying")) {
                        val isPlaying = data.optBoolean("isPlaying")
                        if (isPlaying != musicRepository.isPlaying.value) {
                            if (isPlaying) musicRepository.play(fromRemote = true) else musicRepository.pause(fromRemote = true)
                        }
                    }
                    if (data.has("currentTime")) {
                        val currentTime = data.optLong("currentTime")
                        if (currentTime != musicRepository.currentPosition.value) {
                            musicRepository.seekTo(currentTime, fromRemote = true)
                        }
                    }
                    if (data.has("currentMusicId")) {
                        val currentMusicId = data.optInt("currentMusicId")
                        if (currentMusicId != musicRepository.currentSongIndex.value) {
                            musicRepository.currentSongIndex.value = currentMusicId
                        }
                    }
                    if (data.has("musicList")) {
                        val musicListJson = data.optJSONArray("musicList")?.toString() ?: "[]"
                        val musicList = try {
                            Json.decodeFromString<List<MusicJson>>(musicListJson)
                        } catch (e: Exception) {
                            Log.e("SocketViewModel", "Error decoding musicList", e)
                            emptyList()
                        }
                        musicRepository.replaceWaitingList(musicList, fromRemote = true)
                    }

                }
            }
        }
        socketIORepository.on("app_error") { args ->
            Log.e("SocketIORepository", ">>> [Error] INCOMING: ${args.joinToString()}")
        }

        // to check
        socketIORepository.on("userDisconnected") { args ->
            Log.d("SocketIORepository", ">>> [userDisconnected] INCOMING: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                val userId = data.optString("deviceId")
                uiMessageManager.showMessage("User $userId disconnected from the music room")
            }
        }

        // to check
        socketIORepository.on("disconnectFromDevice") { args ->
            Log.d("SocketIORepository", ">>> [disconnectFromDevice] INCOMING: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                val userId = data.optString("deviceId")
                uiMessageManager.showMessage("The music room ended")
            }
        }
    }

    fun answerRequest(yes: Boolean) {
        val request = _incomingRequest.value ?: return

        viewModelScope.launch {
            // I am the host
            musicRepository.remoteControlHost.value = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
            // manually activate the remote control since on host
            musicRepository.isRemoteControl.value = true

            // Prepare the "data" object with current player state
            val musicData = JSONObject().apply {
                put("isPlaying"  , musicRepository.isPlaying.value)
                put("currentTime", musicRepository.currentPosition.value)

                val ids = JSONArray()
                musicRepository.waitingList.value.forEach { ids.put(it.id) }
                put("musicListIds", ids)

                // Get our own device ID from settings
                val myDeviceId = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
                put("deviceId", myDeviceId)

                // Get the current music index
                put("currentMusicId", musicRepository.currentSongIndex.value)
            }

            // Prepare the full response object
            val response = if (yes) JSONObject().apply {
                    put("emitDeviceID", request.deviceId)
                    put("emitUserId"  , request.userId)
                    put("isAccepted"  , true)
                    put("data"        , musicData)
                }
                else JSONObject().apply {
                    put("isAccepted"  , false)
                }

            Log.d("SocketViewModel", "Sending hostResponse: $response")
            socketIORepository.emit("hostResponse", response)

            _incomingRequest.value = null
        }
    }

    fun quitMusicRoom() { viewModelScope.launch {
        val deviceId = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
        socketIORepository.emit("disconnectFromDevice", deviceId)
        musicRepository.stopRemoteControl()
        _isInRoom.value = false
        uiMessageManager.showMessage("You left the music room")
    }}

    fun connectSocket() = socketIORepository.connect()
}
