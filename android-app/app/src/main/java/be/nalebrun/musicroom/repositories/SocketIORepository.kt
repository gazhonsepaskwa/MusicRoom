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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ISocketIORepository {
    val isConnected: StateFlow<Boolean>
    fun connect()
    fun disconnect()
    fun emit(event: String, vararg args: Any)
    fun on(event: String, listener: (Array<Any>) -> Unit)
    fun off(event: String)
}

@Singleton
class SocketIORepository @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val credentialRepository: ICredentialRepository,
) : ISocketIORepository {

    private var socket: Socket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val listeners = mutableMapOf<String, (Array<Any>) -> Unit>()

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
        Log.d("SocketIORepository", "connect() called. Current status: connected=${socket?.connected()}")
        if (socket?.connected() == true) return

        scope.launch {
            // url and jwt
            Log.d("SocketIORepository", "Attempting to fetch base URL and JWT...")
            val baseUrl = settingsRepository.serverUrlFlow.firstOrNull() ?: run {
                Log.e("SocketIORepository", "Could not connect: Base URL is null")
                return@launch
            }
            val jwt = credentialRepository.jwtFlow.firstOrNull() ?: ""
            val deviceUuid = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""

            // auth & build
            val headers = mapOf(
                "authorization" to listOf(jwt),
                "device" to listOf(deviceUuid)
            )
            val options = IO.Options.builder()
                .setExtraHeaders(headers)
                .build()

            try {
                val url = if (baseUrl.startsWith("http")) baseUrl else "https://$baseUrl"
                Log.d("SocketIORepository", "Initializing Socket.IO for $url")
                
                socket = IO.socket(url, options)

                // Re-apply all registered listeners
                listeners.forEach { (event, listener) ->
                    Log.d("SocketIORepository", "Applying stored listener for event: '$event'")
                    socket?.on(event) { args ->
                        Log.d("SocketIORepository", "<<< RECEIVED EVENT: '$event' | DATA: ${args?.joinToString()}")
                        listener(args ?: emptyArray())
                    }
                }
                
                socket?.on(Socket.EVENT_CONNECT) {
                    Log.d("SocketIORepository", "Successfully connected to $url")
                    _isConnected.value = true
                }

                socket?.on(Socket.EVENT_DISCONNECT) { args ->
                    val reason = args.getOrNull(0)
                    Log.d("SocketIORepository", "Disconnected from $url. Reason: $reason")
                    _isConnected.value = false
                }

                socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    val error = args.getOrNull(0)
                    Log.e("SocketIORepository", "Connection Error: $error")
                }

                // temporary logger
                socket?.on("hostRequest") { args -> Log.d("SocketIORepository", ">>> [hostRequest] INCOMING: ${args?.joinToString()}") }
                socket?.on("hostResponse") { args -> Log.d("SocketIORepository", ">>> [hostResponse] INCOMING: ${args?.joinToString()}") }
                socket?.on("connectToDevice") { args -> Log.d("SocketIORepository", ">>> [connectToDevice] INCOMING: ${args?.joinToString()}") }

                socket?.on("error") { args ->
                    Log.e("SocketIORepository", "General Error: ${args.getOrNull(0)}")
                }

                Log.d("SocketIORepository", "Calling socket.connect()")
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
        if (socket?.connected() == true) {
            socket?.emit(event, *args)
        } else {
            Log.w("SocketIORepository", "Cannot emit event '$event': Socket is not connected")
        }
    }

    override fun on(event: String, listener: (Array<Any>) -> Unit) {
        Log.d("SocketIORepository", "Registering listener for event: '$event' (Socket state: ${if (socket == null) "NULL" else "READY"})")
        listeners[event] = listener
        
        // If socket exists, attach it now
        socket?.on(event) { args ->
            Log.d("SocketIORepository", "<<< RECEIVED EVENT: '$event' | DATA: ${args?.joinToString()}")
            listener(args ?: emptyArray())
        }
    }

    override fun off(event: String) {
        Log.d("SocketIORepository", "Removing listener for event: '$event'")
        listeners.remove(event)
        socket?.off(event)
    }
}
