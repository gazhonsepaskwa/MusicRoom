package be.nalebrun.musicroom.ui.element

import androidx.activity.compose.LocalActivity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

@Preview
@Composable
fun MiniPlayer() {
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val musicViewModel: MusicViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val currentSong: Int by musicViewModel.currentSong.collectAsStateWithLifecycle()
    val musicJson: MusicJson by musicViewModel.music.collectAsStateWithLifecycle()
    val playing by musicViewModel.isPlaying.collectAsStateWithLifecycle()

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
            modifier = Modifier.clickable(onClick = {
                navigationViewModel.navigateTo("music-player")
            })
        ) {
            Text(musicJson.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(musicJson.artists.firstOrNull()?.title ?: "Unknown Artist", fontSize = 13.sp)
        }

        // music control
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = "",
                Modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        musicViewModel.goToPreviousSong()
                    })
            )
            Image(
                painter = painterResource(id =
                    if (playing) R.drawable.outline_pause_24
                    else         R.drawable.outline_play_arrow_24
                ),
                contentDescription = "",
                Modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        if (playing) musicViewModel.pause() else musicViewModel.play()
                    })
            )
            Image(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = "",
                Modifier
                    .size(30.dp)
                    .clickable(onClick = {
                        musicViewModel.goToNextSong()
                    })
            )
        }
    }
}