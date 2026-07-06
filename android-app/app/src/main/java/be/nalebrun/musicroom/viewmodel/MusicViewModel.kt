package be.nalebrun.musicroom.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.apiMusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.AlbumJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MusicViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val _waitingList = MutableStateFlow(listOf(1, 2, 5))
    private val _currentSongIndex = MutableStateFlow(0)

    private var currentPlayingId: Int? = null
    private var isFirstLoad = true

    val player = ExoPlayer.Builder(context).build()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                _currentPosition.value = player.currentPosition
                _duration.value = player.duration.coerceAtLeast(0L)
                _isPlaying.value = player.isPlaying
                if (currentPosition.value > _duration.value - 100 && _duration.value > 0) {
                    // skip to next song 100 ms before the end
                    goToNextSong()
                }
                delay(1000.milliseconds)
            }
        }
    }

    private val _music = MutableStateFlow(apiMusicJson(id = 0, title = "", album = AlbumJson(title = "", images = listOf("", "", "")), duration = 0))
    val         music : StateFlow<apiMusicJson> = _music

    val currentSong: StateFlow<Int> = combine(
        _waitingList,
        _currentSongIndex
    ) { list, index ->
        if (index in list.indices) list[index] else list.firstOrNull() ?: 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _waitingList.value.firstOrNull() ?: 0
    )


    // methode
    fun goToNextSong() {
        _currentSongIndex.value =
            if (_currentSongIndex.value >= _waitingList.value.size - 1)
                0 // loop the list for testing purpose
            else
                _currentSongIndex.value + 1
    }
    fun goToPreviousSong() {
        _currentSongIndex.value =
            if (_currentSongIndex.value == 0)
                _waitingList.value.size - 1
            else
                _currentSongIndex.value - 1
    }

    fun fetchMusicById(id : Int) {
        if (id == currentPlayingId) return

        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                apiRepository.get(
                    url = "https://musicroom.nalebrun.be/music/$id",
                    auth = "Bearer $jwt",
                    onResponse = { _, response ->
                        if (response.code in 200..<300) {
                            val body = response.body?.string() ?: ""
                            viewModelScope.launch {
                                _music.value = Json.decodeFromString<apiMusicJson>(body)
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

    @OptIn(UnstableApi::class)
    private fun playStream(id: Int, jwt: String, autoPlay: Boolean = true) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $jwt"))
        
        val mediaItem = MediaItem.fromUri("https://musicroom.nalebrun.be/music/stream/$id")
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
        player.prepare()
        if (autoPlay) {
            player.play()
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}