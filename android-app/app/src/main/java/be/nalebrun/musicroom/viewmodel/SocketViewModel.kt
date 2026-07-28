package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import be.nalebrun.musicroom.repositories.IMusicRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import be.nalebrun.musicroom.repositories.ISocketIORepository
import be.nalebrun.musicroom.repositories.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
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
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _incomingRequest = MutableStateFlow<ConnectionRequest?>(null)
    val incomingRequest = _incomingRequest.asStateFlow()

    init {
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
    }

    fun acceptRequest() {
        val request = _incomingRequest.value ?: return
        
        // Prepare the "data" object with current player state
        val musicData = JSONObject().apply {
            put("isPlaying", musicRepository.isPlaying.value)
            put("currentTime", musicRepository.currentPosition.value / 1000) // seconds
            
            val waitingListIds = JSONArray()
            (musicRepository as? MusicRepository)?.waitingList?.value?.forEach { waitingListIds.put(it.id) }
            put("musicListIds", waitingListIds)
            
            // Get our own device ID from settings
            val myDeviceId = runBlocking { settingsRepository.deviceUuidFlow.firstOrNull() } ?: ""
            put("deviceId", myDeviceId)
        }

        // Prepare the full response object
        val response = JSONObject().apply {
            put("emitDeviceID", request.deviceId)
            put("emitUserId", request.userId)
            put("data", musicData)
        }

        Log.d("SocketViewModel", "Sending hostResponse: $response")
        socketIORepository.emit("hostResponse", response)
        
        dismissRequest()
    }

    fun connectSocket() = socketIORepository.connect()

    fun dismissRequest() {
        _incomingRequest.value = null
        // TODO send no to the sender
    }
}
