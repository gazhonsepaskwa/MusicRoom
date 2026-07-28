package be.nalebrun.musicroom.repositories

import android.content.ComponentName
import android.content.Context
import android.net.Uri
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
import coil3.toUri

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


    // music playback

    /**
     * Fetch on the api the music details by id
     */
    fun fetchMusicById(id: Int)

    /**
     * go to the next song in the waiting list
     */
    fun goToNextSong()
    /**
     * go to the previous song in the waiting list
     */
    fun goToPreviousSong()

    /**
     * play the music via the controller
     */
    fun play()

    /**
     * pause the music via the controller
     */
    fun pause()
    /**
     * seek to a position in the music via the controller
     */
    fun seekTo(newPosition: Long)


    // Waiting list control

    /**
     * Add a song to the waiting list after the song that is currently being played (or fetched)
     */
    fun addSongToWaitingListNext(music: MusicJson)

    /**
     * Add a song to the waiting list at the end of the list
     */
    fun addSongToWaitingListEnd(music: MusicJson)

    /**
     * Remove a song from the waiting list by its music object
     */
    fun removeSongFromWaitingListByMusic(music: MusicJson)

    /**
     * remove a song from the waiting list by its index in the waiting list
     * ex : to remove "La fin de nation glory" delete index=1.
     * 0 - Take a hint,
     * 1 - La fin de nation glory,
     * 2 - Moog City,
     * 3 - Test Drive,
     * 4 - ...
     */
    fun removeSongFromWaitingListByIndex(index: Int)

    /**
     * clear the waiting list
     */
    fun clearWaitingList()

    /**
     * replace the actual waiting list by the one given as parameter
     */
    fun replaceWaitingList(newWaitingList: List<MusicJson>)

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
    private val                     credentialRepository:   CredentialRepository
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
    private val _currentSongIndex = MutableStateFlow(0)

    // bool that prevent multiple setups
    private var isFirstLoad = true

    // scope for coroutines
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // id of the song that is playing now (guard to don't fetch 2 time the same song)
    // always the real id of the song that is playing. Not affected by fetching delay
    private var currentPlayingId: Int? = null

    override val currentSong: StateFlow<Int> = combine(
        flow  = _waitingList,
        flow2 = _currentSongIndex
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

    override fun goToNextSong() {
        _currentSongIndex.value =
            if (_currentSongIndex.value >= _waitingList.value.size - 1)
                0 // loop the list for the moment
                // TODO : see what else can be done (ex: smart suggestions)
            else
                _currentSongIndex.value + 1
    }

    override fun goToPreviousSong() {
        _currentSongIndex.value =
            if (_currentSongIndex.value == 0)
                _waitingList.value.size - 1
                // loop the list for the moment
                // TODO : see what else can be done (ex: smart suggestions)
            else
                _currentSongIndex.value - 1
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

    override fun play() {
        controller?.play()
    }
    override fun pause() {
        controller?.pause()
    }
    override fun seekTo(newPosition: Long) {
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

    override fun addSongToWaitingListEnd(music : MusicJson) {
        _waitingList.value += music
    }

    override fun addSongToWaitingListNext(music: MusicJson) {
        if (_waitingList.value.isNotEmpty()) {
            val currentList = _waitingList.value.toMutableList()
            val insertIndex = _currentSongIndex.value + 1
            currentList.add(insertIndex, music)
            _waitingList.value = currentList
        } else {
            _waitingList.value = listOf(music)
            _currentSongIndex.value = 0
        }
    }

    override fun removeSongFromWaitingListByMusic(music: MusicJson) {
        val index = _waitingList.value.indexOf(music)
        removeSongFromWaitingListByIndex(index)
    }

    override fun removeSongFromWaitingListByIndex(index: Int) {
        val currentList = _waitingList.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _waitingList.value = currentList
        }
    }

    override fun clearWaitingList() {
        _waitingList.value = emptyList()
    }

    override fun replaceWaitingList(newWaitingList: List<MusicJson>) {
        _waitingList.value = newWaitingList
    }

    override fun release() {
        MediaController.releaseFuture(controllerFuture)
    }
}