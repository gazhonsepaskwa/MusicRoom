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
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

interface IMusicRepository {
    val music: StateFlow<MusicJson>
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val currentSong: StateFlow<Int>


    // music playback

    fun fetchMusicById(id: Int)
    fun goToNextSong()
    fun goToPreviousSong()
    fun play()
    fun pause()
    fun seekTo(newPosition: Long)


    // Waiting list control

    fun addSongToWaitingListNext(music: MusicJson)

    fun addSongToWaitingListEnd(music: MusicJson)

    fun removeSongFromWaitingListByMusic(music: MusicJson)

    fun removeSongFromWaitingListByIndex(index: Int)

    fun clearWaitingList()

    fun replaceWaitingList(newWaitingList: List<MusicJson>)

    // other
    fun release()
}

@OptIn(UnstableApi::class)
@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext private val context:                Context,
    private val                     apiRepository:          APIRepository,
    private val                     credentialRepository:   CredentialRepository
) : IMusicRepository {

    // controller
    private val sessionToken                    = SessionToken(context, ComponentName(context, PlaybackService::class.java))
    private val controllerFuture                = MediaController.Builder(context, sessionToken).buildAsync()
    private var controller: MediaController?    = null

    // music data
    private val _music = MutableStateFlow(MusicJson(id = 0, title = "", album = AlbumJson(
        title = "",
        images = listOf("", "", "")
    ), duration = 0))
    override val music : StateFlow<MusicJson> = _music

    // Playing state
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    // Position in the music in ms
    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition = _currentPosition.asStateFlow()

    // Song duration in ms
    private val _duration = MutableStateFlow(0L)
    override val duration = _duration.asStateFlow()

    // Waiting list
    private val _waitingList = MutableStateFlow<List<MusicJson>>(emptyList())
    val waitingList: StateFlow<List<MusicJson>> = _waitingList
    private val _currentSongIndex = MutableStateFlow(0)

    // bool that prevent multiple setups
    private var isFirstLoad = true

    // scope for coroutines
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // id of the song that is playing now (guard to don't fetch 2 time the same song)
    private var currentPlayingId: Int? = null

    // song supposed to play now (It may be not accurate if the song is currently being fetched)
    override val currentSong: StateFlow<Int> = combine(
        _waitingList,
        _currentSongIndex
    ) { list, index ->
        if (index in list.indices) list[index].id else list.firstOrNull()?.id ?: 0
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _waitingList.value.firstOrNull()?.id ?: 0
    )

    // player listener
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }
        override fun onPlaybackStateChanged(playbackState: Int) {
            controller?.let {
                _duration.value = it.duration.coerceAtLeast(0L)
            }
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
                0 // loop the list for testing purpose
            else
                _currentSongIndex.value + 1
    }

    override fun goToPreviousSong() {
        _currentSongIndex.value =
            if (_currentSongIndex.value == 0)
                _waitingList.value.size - 1
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
                                _music.value = Json.decodeFromString<MusicJson>(body)
                                currentPlayingId = id
                                val shouldPlay = if (isFirstLoad) {
                                    isFirstLoad = false
                                    false
                                } else {
                                    true
                                }
                                playStream(id, jwt, shouldPlay)
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
    fun playStream(id: Int, jwt: String, autoPlay: Boolean = true) {
        val metadata = MediaMetadata.Builder()
            .setTitle(_music.value.title)
            .setAlbumTitle(_music.value.album?.title)
            .build()
        // TODO : add the artist and cover art

        val baseUrl = runBlocking { apiRepository.getBaseUrl() }
        val mediaItem = MediaItem.Builder()
            .setUri("https://$baseUrl/music/stream/$id")
            .setMediaMetadata(metadata)
            .build()

        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        if (autoPlay) {
            controller?.play()
        }
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