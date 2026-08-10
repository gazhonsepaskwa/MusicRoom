package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.ui.element.QueueBottomSheet
import be.nalebrun.musicroom.ui.element.DeviceControlBottomSheet
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import be.nalebrun.musicroom.viewmodel.DevicesViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import coil3.compose.AsyncImage
import androidx.compose.runtime.collectAsState
import be.nalebrun.musicroom.repositories.SocketIORepository
import be.nalebrun.musicroom.viewmodel.SocketViewModel

enum class Repeat{
    NO,
    ONE,
    YES
}

@Preview
@Composable
fun MusicPlayerUi() {

    val activity = LocalActivity.current
    val musicViewModel: MusicViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val socketViewModel: SocketViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val DevicesViewModel: DevicesViewModel = hiltViewModel()

    val currentSong: Int by musicViewModel.currentSong.collectAsStateWithLifecycle()

    var lyrics  by remember { mutableStateOf(""      ) }

    val musicJson: MusicJson by musicViewModel.music.collectAsStateWithLifecycle()

    var showQueueSheet by remember { mutableStateOf(false) }
    var showDeviceSheet by remember { mutableStateOf(false) }

    // on song change, fetch the info of the next song
    LaunchedEffect(currentSong) {
        musicViewModel.fetchMusicById(currentSong)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PageTopBackButton(onClick = { navigationViewModel.navigateBack() })

        CoverArt(musicJson.album?.images?.getOrNull(1) ?: "", lyrics) // take the second image because the screen quality is low

        // Title and artist
        Column() {
            Text(musicJson.title, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(musicJson.artists.firstOrNull()?.title ?: "Unknown Artist")
        }

        SongProgressBar(musicViewModel)

        MusicControlButtons(musicViewModel)

        BottomButtons(
            onQueueClick = { showQueueSheet = true },
            onControlDeviceClick = {
                if (socketViewModel.isInRoom.value) {
                    socketViewModel.quitMusicRoom()
                    navigationViewModel.navigateTo("search")
                } else {
                    showDeviceSheet = true
                }
            }
        )
    }

    if (showQueueSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        QueueBottomSheet(
            musicViewModel,
            onDismissRequest = { showQueueSheet = false })
    }

    if (showDeviceSheet) {
        DeviceControlBottomSheet(
            viewModel = DevicesViewModel,
            onDismissRequest = { showDeviceSheet = false }
        )
    }
}

@Composable
fun CoverArt(url: String, lyrics: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
            Box(
            modifier = Modifier
                .offset(x = 50.dp)
                .size(200.dp)
                .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = lyrics,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Right
            )
        }

        // Front Box (Image)
        Box(
            modifier = Modifier
                .offset(x = (-30).dp)
                .size(240.dp)
                .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = url,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongProgressBar(viewModel: MusicViewModel) {
    val songNow by viewModel.currentPosition.collectAsStateWithLifecycle()
    val songEnd by viewModel.duration.collectAsStateWithLifecycle()

    Column() {
        val sliderPosition = if (songEnd > 0) songNow.toFloat() / songEnd.toFloat() else 0f
        Slider(
            value = sliderPosition,
            onValueChange = {
                val newPosition = (it * songEnd).toLong()
                viewModel.seekTo(newPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor = MaterialTheme.colorScheme.onBackground,
                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.background), CircleShape)
                )
            },
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sliderPosition)
                        .height(8.dp)
                        .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground), CircleShape)
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(msToReadableString(songNow), fontSize = 10.sp)
            Text(msToReadableString(songEnd), fontSize = 10.sp)
        }
    }
}

@Composable
fun MusicControlButtons(viewModel: MusicViewModel) {

    var repeat  by remember { mutableStateOf(Repeat.NO) }
    var shuffle by remember { mutableStateOf(false    ) }
    val play     =viewModel.isPlaying

    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val controlIconsSize: Dp = 40.dp
        Icon(
            painter = painterResource(id = when {
                shuffle -> R.drawable.outline_shuffle_on_24
                else -> R.drawable.outline_shuffle_24
            } as Int ),
            contentDescription = "",
            Modifier
                .size(controlIconsSize)
                .clickable(onClick = {
                    shuffle = !shuffle
                }),
            tint = if (shuffle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = "",
                Modifier
                    .size(controlIconsSize)
                    .clickable(onClick = {
                        viewModel.goToPreviousSong()
                    }),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                painter = painterResource(id = when {
                    play.collectAsState().value -> R.drawable.outline_pause_24
                    else -> R.drawable.outline_play_arrow_24
                }),
                contentDescription = "",
                Modifier
                    .size(controlIconsSize)
                    .clickable(onClick = {
                        if (play.value) viewModel.pause() else viewModel.play()
                    }),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = "",
                Modifier
                    .size(controlIconsSize)
                    .clickable(onClick = {
                        viewModel.goToNextSong()
                    }),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            painter = painterResource(id = when (repeat) {
                Repeat.NO  -> R.drawable.outline_repeat_24
                Repeat.ONE -> R.drawable.outline_repeat_one_24
                Repeat.YES -> R.drawable.outline_repeat_on_24
            }),
            contentDescription = "",
            Modifier
                .size(controlIconsSize)
                .clickable(onClick = {
                    // go to next state
                    repeat = Repeat.entries.toTypedArray()[(repeat.ordinal + 1) % Repeat.entries.size]
                }),
            tint = if (repeat != Repeat.NO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BottomButtons(onQueueClick: () -> Unit, onControlDeviceClick: () -> Unit) {
    val activity = LocalActivity.current
    val socketViewModel: SocketViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val isInRoom = socketViewModel.isInRoom.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Control device Button
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .border(border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground), shape = CircleShape)
                .clickable { onControlDeviceClick() }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_devices_other_24),
                contentDescription = "",
                Modifier.size(25.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(if (isInRoom.value) "Disconnect" else "Control device")
        }

        // musique queue
        Icon(
            painter = painterResource(id = R.drawable.outline_event_list_24),
            contentDescription = "",
            Modifier
                .size(25.dp)
                .clickable(onClick = onQueueClick),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

fun msToReadableString(msTime: Long): String {
    val totalSeconds = msTime / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
