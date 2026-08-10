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
import org.json.XML
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

        // (controller) receiving the connection to host answer
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
                }
            } else {
                Log.w("SocketViewModel", "Received hostResponse but data is not a JSONObject")
            }
        }

        socketIORepository.on("playback_state") { args ->
            Log.d("SocketIORepository", ">>> [hostRequest] INCOMING: ${args.joinToString()}")
        }

        // to check
        socketIORepository.on("userDisconnected") { args ->
            Log.d("SocketIORepository", ">>> [hostRequest] INCOMING: ${args.joinToString()}")
            val data = args.getOrNull(0)
            if (data is JSONObject) {
                val userId = data.optString("deviceId")
                uiMessageManager.showMessage("User $userId disconnected from the music room")
            }
        }

        // to check
        socketIORepository.on("disconnectFromDevice") { args ->
            Log.d("SocketIORepository", ">>> [hostRequest] INCOMING: ${args.joinToString()}")
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
            // Prepare the "data" object with current player state
            val musicData = JSONObject().apply {
                put("isPlaying"  , musicRepository.isPlaying.value)
                put("currentTime", musicRepository.currentPosition.value) // ms or sec ?

                val waitingListIds = JSONArray()
                musicRepository.waitingList.value.forEach { waitingListIds.put(it.id) }
                put("musicListIds", waitingListIds)

                // Get our own device ID from settings
                val myDeviceId = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
                put("deviceId", myDeviceId)
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
