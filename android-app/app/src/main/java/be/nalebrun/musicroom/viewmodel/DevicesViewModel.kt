package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.ForeignDevice
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.repositories.SocketIORepository
import be.nalebrun.musicroom.repositories.IMusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * The logic for the Devices page
 * @author nalebrun
 */
@HiltViewModel
class DevicesViewModel @Inject constructor(
    val credentialRepository: CredentialRepository,
    val apiRepository: APIRepository,
    val socketIORepository: SocketIORepository,
    val musicRepository: IMusicRepository
) : ViewModel() {

    // List of available devices that can be controlled or used for playback
    private val _devices = MutableStateFlow<List<ForeignDevice>>(emptyList())
    val devices: StateFlow<List<ForeignDevice>> = _devices

    // Current remote control permissions
    val canTogglePlayPause = musicRepository.canTogglePlayPause.asStateFlow()
    val canModifyMusic     = musicRepository.canModifyMusic.asStateFlow()
    val canSeek            = musicRepository.canSeek.asStateFlow()

    init {
        // Observe remote control state to reset permissions when it ends
        viewModelScope.launch {
            musicRepository.isRemoteControl.collect { isRemote ->
                if (!isRemote) {
                    musicRepository.canTogglePlayPause.value = true
                    musicRepository.canModifyMusic.value     = true
                    musicRepository.canSeek.value            = true
                }
            }
        }
    }

    /**
     * Fetch the list of available devices from the server
     */
    fun fetchAvailableDevices() {
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    apiRepository.get(
                        "/devices/available",
                        "Bearer $jwt",
                        { _, response ->
                            if (response.code in 200..<300) {
                                val body = response.body?.string()
                                if (body != null) {
                                    try {
                                        val parsedResults = Json.decodeFromString<List<ForeignDevice>>(body)
                                        _devices.value = parsedResults
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        },
                        { _, e ->
                            e.printStackTrace()
                        }
                    )
                }
            }
        }
    }

    /**
     * Ask to take control of another device playback.
     * Message send via the websocket
     */
    fun askDeviceControl(device: ForeignDevice) {
        musicRepository.canTogglePlayPause.value = device.canTogglePlayPause
        musicRepository.canModifyMusic.value     = device.canModifyMusic
        musicRepository.canSeek.value            = device.canSeek
        viewModelScope.launch {
            socketIORepository.emit("connectToDevice", device.deviceId)
        }
    }

}
