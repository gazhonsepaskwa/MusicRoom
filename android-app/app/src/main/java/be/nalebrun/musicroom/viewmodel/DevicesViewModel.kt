package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.ForeignDevice
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.repositories.SocketIORepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * The logic for the Devices page
 * @author nalebrun
 */
@HiltViewModel
class DevicesViewModel @Inject constructor(
    val credentialRepository: CredentialRepository,
    val apiRepository: APIRepository,
    val socketIORepository: SocketIORepository
) : ViewModel() {

    // List of available devices that can be controlled or used for playback
    private val _devices = MutableStateFlow<List<ForeignDevice>>(emptyList())
    val devices: StateFlow<List<ForeignDevice>> = _devices

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
        viewModelScope.launch {
            socketIORepository.emit("connectToDevice", device.deviceId)
        }
    }
}