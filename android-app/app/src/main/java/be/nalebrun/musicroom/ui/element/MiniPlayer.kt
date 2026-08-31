package be.nalebrun.musicroom.ui.element

import android.app.Activity
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.DevicesViewModel

@Composable
fun MiniPlayer(musicViewModel: MusicViewModel, activity: Activity) {
    val navigationViewModel: NavigationViewModel = hiltViewModel(activity as ViewModelStoreOwner)
    val devicesViewModel: DevicesViewModel = hiltViewModel(activity as ViewModelStoreOwner)

    val currentSong: Int by musicViewModel.currentSong.collectAsStateWithLifecycle()
    val musicJson: MusicJson by musicViewModel.music.collectAsStateWithLifecycle()
    val playing by musicViewModel.isPlaying.collectAsStateWithLifecycle()

    val canTogglePlayPause by devicesViewModel.canTogglePlayPause.collectAsStateWithLifecycle()
    val canModifyMusic by devicesViewModel.canModifyMusic.collectAsStateWithLifecycle()

    // on song change, fetch the info of the next song
    LaunchedEffect(currentSong) {
        musicViewModel.fetchMusicById(currentSong)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // song info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
                .clickable(onClick = {
                    navigationViewModel.navigateTo("music-player")
                })
        ) {
            Text(
                text = musicJson.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            Text(
                text = musicJson.artists.firstOrNull()?.title ?: "Unknown Artist",
                fontSize = 13.sp,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }

        // music control
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        enabled = canModifyMusic,
                        onClick = {
                            musicViewModel.goToPreviousSong()
                        }
                    ),
                tint = if (canModifyMusic) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Icon(
                painter = painterResource(id =
                    if (playing) R.drawable.outline_pause_24
                    else         R.drawable.outline_play_arrow_24
                ),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        enabled = canTogglePlayPause,
                        onClick = {
                            if (playing) musicViewModel.pause() else musicViewModel.play()
                        }
                    ),
                tint = if (canTogglePlayPause) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
            Icon(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = "",
                modifier = Modifier
                    .size(30.dp)
                    .clickable(
                        enabled = canModifyMusic,
                        onClick = {
                            musicViewModel.goToNextSong()
                        }
                    ),
                tint = if (canModifyMusic) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}