package be.nalebrun.musicroom.repositories

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.AlbumJson
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.services.PlaybackService
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.net.toUri
import org.json.JSONObject
import org.json.JSONArray

/**
 * Repository to manage the music.
 * implement a custom waiting list and a music controller for the service
 * @see MusicService
 * @author :nalebrun
 */
interface IMusicRepository {
    /**
     * The music that is currently playing
     */
    val music: StateFlow<MusicJson>

    /**
     * The playing state
     */
    val isPlaying: StateFlow<Boolean>

    /**
     * The position in the music in ms
     */
    val currentPosition: StateFlow<Long>

    /**
     * The duration of the music in ms
     */
    val duration: StateFlow<Long>

    /**
     * The music waiting list
     */
    val waitingList: StateFlow<List<MusicJson>>

    /**
     * id of the song supposed to play now
     * (It may be not accurate if the song is currently being fetched)
     * ex : if the song is beeing fetched, the song that the player play is still the previous song
     * but this var will be the id of the song that is currently being fetched
     */
    val currentSong: StateFlow<Int>

    /**
     * The index of the current song in the waiting list
     */
    val currentSongIndex: MutableStateFlow<Int>

    /**
     * The remote control state
     */
    val isRemoteControl: MutableStateFlow<Boolean>
    /**
     * The host of the remote control
     */
    val remoteControlHost: MutableStateFlow<String>


    // music playback

    /**
     * Fetch on the api the music details by id
     */
    fun fetchMusicById(id: Int)

    /**
     * go to the next song in the waiting list
     */
    fun goToNextSong(fromRemote: Boolean = false)
    /**
     * go to the previous song in the waiting list
     */
    fun goToPreviousSong(fromRemote: Boolean = false)

    /**
     * play the music via the controller
     */
    fun play(fromRemote: Boolean = false)

    /**
     * pause the music via the controller
     */
    fun pause(fromRemote: Boolean = false)
    /**
     * seek to a position in the music via the controller
     */
    fun seekTo(newPosition: Long, fromRemote: Boolean = false)


    // Waiting list control

    /**
     * Add a song to the waiting list after the song that is currently being played (or fetched)
     */
    fun addSongToWaitingListNext(music: MusicJson, fromRemote: Boolean = false)

    /**
     * Add a song to the waiting list at the end of the list
     */
    fun addSongToWaitingListEnd(music: MusicJson, fromRemote: Boolean = false)

    /**
     * Remove a song from the waiting list by its music object
     */
    fun removeSongFromWaitingListByMusic(music: MusicJson, fromRemote: Boolean = false)

    /**
     * remove a song from the waiting list by its index in the waiting list
     * ex : to remove "La fin de nation glory" delete index=1.
     * 0 - Take a hint,
     * 1 - La fin de nation glory,
     * 2 - Moog City,
     * 3 - Test Drive,
     * 4 - ...
     */
    fun removeSongFromWaitingListByIndex(index: Int, fromRemote: Boolean = false)

    /**
     * clear the waiting list
     */
    fun clearWaitingList(fromRemote: Boolean = false)

    /**
     * replace the actual waiting list by the one given as parameter
     */
    fun replaceWaitingList(newWaitingList: List<MusicJson>, fromRemote: Boolean = false)

    /**
     * control another device, instead of the one that is currently playing.
     * exactly the same behavior but without playback
     * @param newIsPlaying   : String "true" or "false"
     * @param newDuration    : Long in ms
     * @param newPosition    : Long in ms
     * @param newWaitingList : List<MusicJson>
     * @param newCurrentSong : Int
     */
    fun startRemoteControl(
        newIsPlaying   : String,
        newPosition    : Long,
        newWaitingList : List<MusicJson>,
        newCurrentSong : Int
    )

    /**
     * stop the remote control
     */
    fun stopRemoteControl()
    // other
    /**
     * release the music controller.
     * to prevent memory leaks
     */
    fun release()
}

@OptIn(UnstableApi::class)
@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context:                Context,
    private val                     apiRepository:          APIRepository,
    private val                     credentialRepository:   CredentialRepository,
    private val                     socketIORepository:     SocketIORepository,
    private val                     settingsRepository:     SettingsRepository,
) : IMusicRepository {

    // controller variables
    private val sessionToken                    = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    private val controllerFuture                = MediaController.Builder(context, sessionToken).buildAsync()
    private var controller: MediaController?    = null

    private val _music = MutableStateFlow(MusicJson(id = 0, title = "", album = AlbumJson(
        title = "",
        images = listOf("", "", "")
    ), duration = 0))
    override val music : StateFlow<MusicJson> = _music

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration = _duration.asStateFlow()

    private val _waitingList = MutableStateFlow<List<MusicJson>>(emptyList())
    override val waitingList = _waitingList.asStateFlow()
    override val currentSongIndex = MutableStateFlow(0)

    // remote control things
    override val isRemoteControl = MutableStateFlow(false)
    override val remoteControlHost = MutableStateFlow("")

    // bool that prevent multiple setups
    private var isFirstLoad = true

    // scope for coroutines
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // id of the song that is playing now (guard to don't fetch 2 time the same song)
    // always the real id of the song that is playing. Not affected by fetching delay
    private var currentPlayingId: Int? = null

    // pending seek and play variables for the controller mode
    private var pendingSeek: Long? = null
    private var pendingPlay: Boolean? = null

    override val currentSong: StateFlow<Int> = combine(
        flow  = _waitingList,
        flow2 = currentSongIndex
    ) { list, index ->
        if (index in list.indices) list[index].id else list.firstOrNull()?.id ?: 0
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _waitingList.value.firstOrNull()?.id ?: 0
    )

    // player listener
    // update the playing state and the duration when the player change
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            controller?.let {
                _duration.value = it.duration.coerceAtLeast(0L)

                // seek to the position when the media is ready
                if (playbackState == Player.STATE_READY) {
                    pendingSeek?.let { seekPos ->
                        it.seekTo(seekPos)
                        pendingSeek = null
                    }
                    pendingPlay?.let { shouldPlay ->
                        if (shouldPlay) it.play() else it.pause()
                        pendingPlay = null
                    }
                }
            }
            // go to the next song if the music is ended
            if (playbackState == Player.STATE_ENDED) {
                goToNextSong()
            }
        }
    }

    init {
        // create the music controller
        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(playerListener)
        }, MoreExecutors.directExecutor())

        // update current position every sec
        scope.launch {
            while (isActive) {
                controller?.let {
                    _currentPosition.value = it.currentPosition
                    _duration.value = it.duration.coerceAtLeast(0L)
                }
                delay(1000.milliseconds)
            }
        }
    }

    override fun goToNextSong(fromRemote: Boolean) {
        // update the current index
        currentSongIndex.value =
            if (currentSongIndex.value >= _waitingList.value.size - 1)
                0 // loop the list for the moment
            // TODO : see what else can be done (ex: smart suggestions)
            else
                currentSongIndex.value + 1

        // propagate the change
        if (isRemoteControl.value && !fromRemote) {
            val json = JSONObject().apply {
                put("currentMusicId", currentSongIndex.value)
                put("currentTime"   , 0)
                put("deviceId"      , remoteControlHost.value)
            }
            socketIORepository.emit("modifyData", json)
            Log.d("SocketViewModel", "Sending modifyData: $json")
        }
    }

    override fun goToPreviousSong(fromRemote: Boolean) {
        // update the current index
        currentSongIndex.value =
            if (currentSongIndex.value == 0)
                _waitingList.value.size - 1
            // loop the list for the moment
            // TODO : see what else can be done (ex: smart suggestions)
            else
                currentSongIndex.value - 1

        // propagate the change
        if (isRemoteControl.value && !fromRemote) {
            val json = JSONObject().apply {

                put("currentMusicId", currentSongIndex.value)
                put("currentTime"   , 0)
                put("deviceId", remoteControlHost.value)
            }
            socketIORepository.emit("modifyData", json)
            Log.d("SocketViewModel", "Sending modifyData: $json")
        }

    }

    override fun fetchMusicById(id : Int) {
        if (id == currentPlayingId) return
        scope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                apiRepository.get(
                    url = "music/$id",
                    auth = "Bearer $jwt",
                    onResponse = { _, response ->
                        if (response.code in 200..<300) {
                            val body = response.body?.string() ?: ""
                            scope.launch {
                                // when api return,
                                // update the music object and the currentPlayingId,
                                // and play if first load. TODO : check, I think it doesn't work
                                _music.value = Json.decodeFromString<MusicJson>(body)
                                currentPlayingId = id
                                val shouldPlay = if (isFirstLoad) {
                                    isFirstLoad = false
                                    false
                                } else {
                                    true
                                }
                                playStream(id, shouldPlay)
                            }
                        }
                    },
                    onFailure = { _, e ->
                        Log.d("API_RESPONSE_ERROR", "error: $e")
                    }
                )
            }
        }
    }

    override fun play(fromRemote: Boolean) {
        pendingPlay = null
        if (isRemoteControl.value && !fromRemote) {
            val json = JSONObject().apply {
                put("isPlaying", true)
                put("deviceId", remoteControlHost.value)
            }
            socketIORepository.emit("modifyData", json)
            Log.d("SocketViewModel", "Sending modifyData: $json")
        }
        controller?.play()
    }
    override fun pause(fromRemote: Boolean) {
        pendingPlay = null
        if (isRemoteControl.value && !fromRemote) {
            val json = JSONObject().apply {
                put("isPlaying", false)
                put("deviceId", remoteControlHost.value)
            }
            socketIORepository.emit("modifyData", json)
            Log.d("SocketViewModel", "Sending modifyData: $json")
        }
        controller?.pause()
    }
    override fun seekTo(newPosition: Long, fromRemote: Boolean) {
        pendingSeek = null
        if (isRemoteControl.value && !fromRemote) {
            val json = JSONObject().apply {
                put("currentTime", newPosition)
                put("deviceId", remoteControlHost.value)
            }
            socketIORepository.emit("modifyData", json)
            Log.d("SocketViewModel", "Sending modifyData: $json")
        }
        controller?.seekTo(newPosition)
    }

    @OptIn(UnstableApi::class)
    fun playStream(id: Int, autoPlay: Boolean = true) {
        // create the metadata for the MediaItem based on my music data class
        val metadata = MediaMetadata.Builder()
            .setTitle      (_music.value.title)
            .setAlbumTitle (_music.value.album?.title)
            .setArtist     (_music.value.artists.joinToString(", ") { it.title })
            .setArtworkUri (_music.value.album?.images[1]?.toUri() ?: "".toUri())
            .build()

        // create the Media Item with the stream url and the metadata
        val baseUrl = runBlocking { apiRepository.getBaseUrl() }
        val mediaItem = MediaItem.Builder()
            .setUri("https://$baseUrl/music/stream/$id")
            .setMediaMetadata(metadata)
            .build()

        // set, prepare, and play the mediaItem via the controller
        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        if (autoPlay) { controller?.play() }
    }

    override fun addSongToWaitingListEnd(music : MusicJson, fromRemote: Boolean) {
        _waitingList.value += music
        emitWaitingListChange(fromRemote)
    }

    override fun addSongToWaitingListNext(music: MusicJson, fromRemote: Boolean) {
        if (_waitingList.value.isNotEmpty()) {
            val currentList = _waitingList.value.toMutableList()
            val insertIndex = currentSongIndex.value + 1
            currentList.add(insertIndex, music)
            _waitingList.value = currentList
        } else {
            _waitingList.value = listOf(music)
            currentSongIndex.value = 0
        }
        emitWaitingListChange(fromRemote)
    }

    override fun removeSongFromWaitingListByMusic(music: MusicJson, fromRemote: Boolean) {
        val index = _waitingList.value.indexOf(music)
        removeSongFromWaitingListByIndex(index, fromRemote)
    }

    override fun removeSongFromWaitingListByIndex(index: Int, fromRemote: Boolean) {
        val currentList = _waitingList.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _waitingList.value = currentList
        }
        emitWaitingListChange(fromRemote)
    }

    override fun clearWaitingList(fromRemote: Boolean) {
        _waitingList.value = emptyList()
        emitWaitingListChange(fromRemote)
    }

    override fun replaceWaitingList(newWaitingList: List<MusicJson>, fromRemote: Boolean) {
        _waitingList.value = newWaitingList
        emitWaitingListChange(fromRemote)
    }

    private fun emitWaitingListChange(fromRemote: Boolean) {
        if (isRemoteControl.value && !fromRemote) {
            val json = JSONObject().apply {
                val ids = JSONArray()
                _waitingList.value.forEach { ids.put(it.id) }
                put("musicListIds", ids)
                put("deviceId"    , remoteControlHost.value)
            }
            socketIORepository.emit("modifyData", json)
            Log.d("SocketViewModel", "Sending modifyData (waitingList): $json")
        }
    }

    override fun startRemoteControl(
        newIsPlaying   : String,
        newPosition    : Long,
        newWaitingList : List<MusicJson>,
        newCurrentSong : Int
    ) {
        // tell the media player to don't play for real and only play on the distant device
        isRemoteControl.value = true

        // TODO : add dummy info in the interface temporarily while it wait. but not sure it's necessary

        // replace waiting list
        replaceWaitingList(newWaitingList, fromRemote = true)

        // change the current song in the waiting list
        currentSongIndex.value = newCurrentSong

        // seek to
        pendingSeek = newPosition
        pendingPlay = (newIsPlaying == "true")

        controller?.let {
            val newSongId = if (newCurrentSong in newWaitingList.indices) newWaitingList[newCurrentSong].id else 0
            if (it.playbackState == Player.STATE_READY && newSongId == currentPlayingId) {
                // seek
                it.seekTo(newPosition)
                pendingSeek = null
                // play pause
                if (pendingPlay == true) it.play() else it.pause()
                pendingPlay = null
            }
        }
    }

    override fun stopRemoteControl() {
        // reset everything
        isRemoteControl.value = false
        remoteControlHost.value = ""
        pendingSeek = null
        pendingPlay = null

        _music.value = MusicJson(id = 0, title = "", album = AlbumJson(
            title = "",
            images = listOf("", "", "")
        ), duration = 0)

        clearWaitingList()

        _isPlaying.value        = false
        currentSongIndex.value = 0
        _currentPosition.value  = 0L
        _duration.value         = 0L
    }

    override fun release() {
        MediaController.releaseFuture(controllerFuture)
    }
}