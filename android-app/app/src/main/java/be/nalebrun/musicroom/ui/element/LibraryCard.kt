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
import be.nalebrun.musicroom.viewmodel.LibraryViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistViewModel
import org.w3c.dom.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryCard(id: Int, title: String, songs: Int, duration: Int, navigationViewModel: NavigationViewModel) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var showChangeName by remember { mutableStateOf(false) }
    var changeNameSheetState = rememberModalBottomSheetState()

    var newName by remember {mutableStateOf("")}
    var newTitle by remember { mutableStateOf(title)}

    val playlistViewModel: PlaylistViewModel = hiltViewModel()
    val libraryViewModel: LibraryViewModel = hiltViewModel()
    val hours = duration / (1000 * 60 * 60)
    val minutes = (duration / (1000 * 60)) % 60
    var time = "${hours}h$minutes"
    if (hours == 0)
        time = "$minutes min"

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "rename playlist",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = { showChangeName = true }
                ),
                ActionItem(
                    label = "delete playlist",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = {
                        libraryViewModel.deletePlaylist(id)
                        showBottomSheet = false
                    }
                )
            ),
            sheetState = sheetState
        )
    }

    if (showChangeName) {
        ModalBottomSheet(
            onDismissRequest = { showChangeName = false },
            sheetState = changeNameSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                CustomTextField(
                    title = "Change playlist name:",
                    text = newName,
                    onValueChange = { newName = it },
                    modifier = Modifier.padding(bottom = 10.dp))
                BlackOrWhiteButton(
                    text = "Confirm",
                    active = false,
                    onClick = {
                        if (newName.isNotBlank()) {
                            playlistViewModel.renamePlaylist(id, newName)
                            newTitle = newName
                            newName = ""
                            showChangeName = false
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .clickable(true, onClick = {
                navigationViewModel.navigateTo("playlist/$id")
            }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column{
            Text(newTitle, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("$songs songs ● $time")
        }
        Image(
            painter = painterResource(R.drawable.outline_more_horiz_24),
            contentDescription = "",
            modifier = Modifier.clickable(true, onClick = { showBottomSheet = true })
        )
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}
