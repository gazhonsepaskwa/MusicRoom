package be.nalebrun.musicroom.services

import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.IMusicRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Service that manage and maintain alive the music playback via the ExoPlayer
 * @author nalebrun
 */
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var musicRepository: IMusicRepository

    @Inject
    lateinit var credentialRepository: ICredentialRepository

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val jwt = runBlocking { credentialRepository.jwtFlow.first() }
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $jwt"))

        // create the ExoPlayer
        val player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this).setDataSourceFactory(dataSourceFactory))
            .build()

        // create the mediaSession
        mediaSession = MediaSession.Builder(this, object : ForwardingPlayer(player) {
            override fun seekToNext()     = musicRepository.goToNextSong()
            override fun seekToPrevious() = musicRepository.goToPreviousSong()

            override fun isCommandAvailable(command: Int): Boolean {
                return  command == COMMAND_SEEK_TO_NEXT     ||
                        command == COMMAND_SEEK_TO_PREVIOUS ||
                        super.isCommandAvailable(command)
            }

            override fun getAvailableCommands(): Player.Commands {
                return super.getAvailableCommands().buildUpon()
                    .add(COMMAND_SEEK_TO_NEXT)
                    .add(COMMAND_SEEK_TO_PREVIOUS)
                    .build()
            }
        }).build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            player.release()
            release() // release the mediaSession
            mediaSession = null
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession
}