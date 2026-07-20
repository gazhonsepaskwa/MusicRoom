package be.nalebrun.musicroom.ui.element

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import coil3.Image
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    musicViewModel: MusicViewModel,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
) {
    val waitingList by musicViewModel.waitingList.collectAsState()
    val currentMusic by musicViewModel.music.collectAsState()
    val currentMusicIndex = waitingList.indexOfFirst { it.id == currentMusic.id }
    val listState = rememberLazyListState()

    // get screen height for auto scroll
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Scroll to the currently playing song
    LaunchedEffect(currentMusicIndex) {
        if (currentMusicIndex >= 0) {
            listState.scrollToItem(currentMusicIndex)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentPadding = PaddingValues(bottom = screenHeight)
        ) {
            items(waitingList) { music ->
                if (music.id == currentMusic.id) {
                    WaitingListElem(music, isPlaying = true)
                } else {
                    WaitingListElem(music, isPlaying = false)
                }
            }
        }
    }
}

@Composable
fun WaitingListElem(music: MusicJson, isPlaying: Boolean) {
    Column(
        modifier = Modifier
            .padding(bottom = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column() {
                // title
                if (music.title.isNotEmpty())   { Text(music.title) }
                else                            { Text("Unknown Title") }

                // artist
                if (music.artists.isNotEmpty()) {
                    Text(music.artists.firstOrNull()?.title ?: "Unknown Artist")
                }
                else { Text("Unknown Artist") }
            }

            if (isPlaying) {
                Image(
                    painter = rememberDrawablePainter(drawable = getDrawable(LocalContext.current, R.drawable.audio_wave)),
                    contentDescription = "...",
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        // separator
        HorizontalDivider(thickness = 0.5f.dp, color = Color.Gray, modifier = Modifier.padding(top = 5.dp))
    }
}