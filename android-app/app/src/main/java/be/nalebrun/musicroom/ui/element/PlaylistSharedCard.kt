package be.nalebrun.musicroom.ui.element

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.libraryJson
import be.nalebrun.musicroom.viewmodel.LibraryViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistAccessViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistViewModel
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSharedCard(music: libraryJson, navigationViewModel: NavigationViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val viewModel: LibraryViewModel = hiltViewModel()
    val profileModel: UserProfileViewModel = hiltViewModel()
    val accessViewModel: PlaylistAccessViewModel = hiltViewModel()
    val hours = music.duration / (1000 * 60 * 60)
    val minutes = (music.duration / (1000 * 60)) % 60
    var time = "${hours}h$minutes"
    if (hours == 0)
        time = "$minutes min"

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "leave playlist",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = {
                        //GET REAL OWN ID
//                        accessViewModel.leavePlaylistAccess(music.id, userId)
                        showBottomSheet = false
                        viewModel.getPlaylists()
                    }
                )
            ),
            sheetState = sheetState
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .clickable(true, onClick = {
                navigationViewModel.navigateTo("playlist/shared/${music.id}")
            }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column{
            Row {
                Image(
                    modifier = Modifier.padding(end = 2.dp),
                    painter = painterResource(R.drawable.outline_group_24),
                    contentDescription = null,
                )
                Text(music.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Text("${music.songs} songs ● $time")
        }
        Image(
            painter = painterResource(R.drawable.outline_more_horiz_24),
            contentDescription = "",
            modifier = Modifier.clickable(true, onClick = { showBottomSheet = true })
        )
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}
