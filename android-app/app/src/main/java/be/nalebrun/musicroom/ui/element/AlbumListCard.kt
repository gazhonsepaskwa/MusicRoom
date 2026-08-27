package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.repositories.MusicRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListCard(musicViewModel: MusicRepository, music: MusicJson) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var addPlaylistSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "add to queue (next)",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = {
                        musicViewModel.addSongToWaitingListNext(music)
                        showBottomSheet = false
                    }
                ),
                ActionItem(
                    label = "add to queue (end)",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = {
                        musicViewModel.addSongToWaitingListEnd(music)
                        showBottomSheet = false
                    }
                ),
                ActionItem(
                    label = "add to playlist",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = { addPlaylistSheet = true }
                )
            ),
            sheetState = sheetState
        )
    }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp, end = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        ) {
            Text(
                text = music.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
            val artists: String = music.artists.joinToString(", ") { it.title }
            Text(
                text = artists,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
        Icon(
            modifier = Modifier.clickable(true, onClick = {
                showBottomSheet = true
            }),
            painter = painterResource(R.drawable.outline_more_horiz_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }

    if (addPlaylistSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        AddPlaylistSheet(
            musicId = music.id,
            onDismissRequest = { addPlaylistSheet = false }
        )
    }
}