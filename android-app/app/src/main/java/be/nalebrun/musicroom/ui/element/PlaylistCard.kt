package be.nalebrun.musicroom.ui.element

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
import androidx.hilt.navigation.compose.hiltViewModel
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.viewmodel.PlaylistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistCard(playlistId: Int, music: MusicJson, modifier: Modifier = Modifier) {
    val viewModel: PlaylistViewModel = hiltViewModel()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "remove from playlist",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = { viewModel.removeSongFromPlaylist(music.id, playlistId) }
                ),
                ActionItem(
                    label = "add to queue (next)",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = { viewModel.musicRepository.addSongToWaitingListNext(music) }
                ),
                ActionItem(
                    label = "add to queue (end)",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = { viewModel.musicRepository.addSongToWaitingListEnd(music) }
                )
            ),
            sheetState = sheetState
        )
    }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp, end = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(music.title, fontWeight = FontWeight.Bold)
            val artists : String = music.artists.joinToString(", ") { it.title }
            Text(artists)
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
}