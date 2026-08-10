package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistNotificationJson
import be.nalebrun.musicroom.apiJsonStruct.responds.libraryJson
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.CustomTextField
import be.nalebrun.musicroom.ui.element.LibraryCard
import be.nalebrun.musicroom.ui.element.PlaylistInvitationCard
import be.nalebrun.musicroom.ui.element.PlaylistSharedCard
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.LibraryViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistAccessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryUi() {
    val viewModel: LibraryViewModel = hiltViewModel()
    val accessViewModel: PlaylistAccessViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationView: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    var showCreatePlaylistSheet by remember { mutableStateOf(false) }
    val createPlaylistSheetState = rememberModalBottomSheetState()

    val playlists: List<libraryJson> by viewModel.playlists.collectAsState()
    val sharedPlaylists: List<libraryJson> by viewModel.sharedPlaylists.collectAsState()
    val invitations: List<PlaylistNotificationJson> by accessViewModel.playlistRequest.collectAsState()

    var newPlaylist by remember { mutableStateOf("") }
    var newPlaylistPublicStatus by remember { mutableStateOf(false) }

    LaunchedEffect(0) {
        accessViewModel.getPlaylistInvitations()
    }

    if (showCreatePlaylistSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreatePlaylistSheet = false },
            sheetState = createPlaylistSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ){
                CustomTextField(
                    title = "Create a new playlist",
                    text = newPlaylist,
                    onValueChange = { newPlaylist = it },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                PermissionToggle(
                    label = "Public",
                    checked = newPlaylistPublicStatus,
                    onCheckedChange = {
                        newPlaylistPublicStatus = it
                    }
                )
                BlackOrWhiteButton(
                    text = "Confirm",
                    active = false,
                    onClick = {
                        if (newPlaylist.isNotBlank()) {
                            viewModel.createPlaylist(newPlaylist, newPlaylistPublicStatus)
                            newPlaylist = ""
                            showCreatePlaylistSheet = false
                            viewModel.getPlaylists()
                        }
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,

            ){
                Text("")
                Text(
                    "Library",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
                Image(
                    painter = painterResource(R.drawable.outline_add_24),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable(true, onClick = { showCreatePlaylistSheet = true })
                )
            }
            HorizontalDivider(thickness = 2.dp, color = Color.Black)
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(playlists) { it ->
                    LibraryCard(it, navigationView)
                }
                if (sharedPlaylists.isNotEmpty()) {
//                    item {
//                        Text(
//                            "Shared playlists:",
//                            modifier = Modifier.padding(10.dp),
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
                    items(sharedPlaylists) { item ->
                        PlaylistSharedCard(item, navigationView)
                    }
                }
                if (invitations.isNotEmpty()) {
                    item {
                        Text(
                            "Invitations to playlists:",
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(invitations) { item ->
                        PlaylistInvitationCard(item)
                    }
                }
            }
        }

        BottomScreenMenu(
            activeScreen = ActiveScreen.LIBRARY,
        )
    }
}