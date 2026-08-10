package be.nalebrun.musicroom.repositories

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ISocketIORepository {

    val isConnected: StateFlow<Boolean>

    /**
     * connect to the socket
     */
    fun connect()

    /**
     * disconnect from the socket
     */
    fun disconnect()

    /**
     * send a message to the socket
     */
    fun emit(event: String, vararg args: Any)

    /**
     * add a listener to the socket
     */
    fun on(event: String, listener: (Array<Any>) -> Unit)

    /**
     * remove a listener from the socket
     */
    fun off(event: String)
}

/**
 * Repository to manage the socket.io connection
 * @author :nalebrun
 */
@Singleton
class SocketIORepository @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val credentialRepository: ICredentialRepository,
) : ISocketIORepository {

    // the socket.io instance
    private var socket: Socket? = null
    // global scope
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val listeners = mutableMapOf<String, (Array<Any>) -> Unit>()

    // reflect the socket connection state
    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    init {
        // Automatically reconnect when JWT or Server URL changes
        scope.launch {
            settingsRepository.serverUrlFlow.collect {
                reconnect()
            }
        }
        scope.launch {
            credentialRepository.jwtFlow.collect {
                reconnect()
            }
        }
    }

    private fun reconnect() {
        if (socket != null) {
            Log.d("SocketIORepository", "Reconnecting due to settings/credential change...")
            disconnect()
            connect()
        }
    }

    override fun connect() {
        if (socket?.connected() == true) return

        scope.launch {
            // get the infos from the storage
            val baseUrl     = settingsRepository.serverUrlFlow.firstOrNull() ?:  run { return@launch }
            val jwt         = credentialRepository.jwtFlow.firstOrNull() ?:      run { return@launch }
            val deviceUuid  = settingsRepository.deviceUuidFlow.firstOrNull() ?: run { return@launch }

            if (jwt.isEmpty()) {
                Log.d("SocketIORepository", "JWT is empty, not connecting")
                return@launch
            }

            // auth headers
            val headers = mapOf(
                "authorization" to listOf(jwt),
                "device"        to listOf(deviceUuid)
            )
            // build
            val options = IO.Options.builder()
                .setExtraHeaders(headers)
                .build()

            try {
                val url = if (baseUrl.startsWith("http")) baseUrl else "https://$baseUrl"
                Log.d("SocketIORepository", "Initializing Socket.IO for $url")

                // Create a new socket instance
                socket = IO.socket(url, options)

                // Re-apply all registered listeners
                listeners.forEach { (event, listener) ->
                    socket?.on(event) { args ->
                        Log.d("SocketIORepository", "<<< RECEIVED EVENT: '$event' | DATA: ${args?.joinToString()}")
                        listener(args ?: emptyArray())
                    }
                }

                ///////////////
                // Listeners //
                ///////////////

                // connect
                socket?.on(Socket.EVENT_CONNECT) {
                    Log.d("SocketIORepository", "Successfully connected to $url")
                    _isConnected.value = true
                }

                // disconnect
                socket?.on(Socket.EVENT_DISCONNECT) { args ->
                    val reason = args.getOrNull(0)
                    Log.d("SocketIORepository", "Disconnected from $url. Reason: $reason")
                    _isConnected.value = false
                }

                // connection error
                socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    val error = args.getOrNull(0)
                    Log.e("SocketIORepository", "Connection Error: $error")
                }

                // General errors
                socket?.on("error") { args ->
                    Log.e("SocketIORepository", "General Error: ${args.getOrNull(0)}")
                }

                // do the actual connection
                socket?.connect()

            } catch (e: Exception) {
                Log.e("SocketIORepository", "Socket initialization failed", e)
            }
        }
    }

    override fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    override fun emit(event: String, vararg args: Any) {
        // only emit if the socket is connected
        if (socket?.connected() == true) {
            socket?.emit(event, *args)
        } else {
            Log.w("SocketIORepository", "Cannot emit event '$event': Socket is not connected")
        }
    }

    override fun on(event: String, listener: (Array<Any>) -> Unit) {
        listeners[event] = listener
        
        socket?.on(event) { args ->
            Log.d("SocketIORepository", "<<< RECEIVED EVENT: '$event' | DATA: ${args?.joinToString()}")
            listener(args ?: emptyArray())
        }
    }

    override fun off(event: String) {
        listeners.remove(event)
        socket?.off(event)
    }
}