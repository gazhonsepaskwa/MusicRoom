package be.nalebrun.musicroom.ui.element

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistNotificationJson
import be.nalebrun.musicroom.viewmodel.LibraryViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistAccessViewModel

@Composable
fun PlaylistInvitationCard(invite: PlaylistNotificationJson) {
    val activity = LocalActivity.current
    val navigationView: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val accessViewModel : PlaylistAccessViewModel = hiltViewModel()
    val libraryViewModel: LibraryViewModel = hiltViewModel()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.playlist_tmp),
                contentDescription = null,
                modifier = Modifier.padding(10.dp)
            )
            Text(invite.playlistName ?: "???", fontWeight = FontWeight.Bold)
//            Text(text = invite.requesterName ?: "???", fontWeight = FontWeight.Bold)
        }
        Row() {
            Image(
                painter = painterResource(R.drawable.outline_expand_circle_down_24),
                contentDescription = "Accept",
                modifier = Modifier.clickable(true, onClick = {
                    if (invite.playlistId != null) {
                        accessViewModel.answerAccessInvitationToPlaylist(
                            invite.playlistId,
                            "ACCEPTED"
                        )
                        navigationView.navigateTo("library")
                    }
                })
            )
            Image(
                painter = painterResource(R.drawable.outline_cancel_24),
                contentDescription = "Refuse",
                modifier = Modifier.clickable(true, onClick = {
                    if (invite.playlistId != null) {
                        accessViewModel.answerAccessInvitationToPlaylist(
                            invite.playlistId,
                            "REJECTED"
                        )
                        navigationView.navigateTo("library")
                    }
                })
            )
        }
    }
}