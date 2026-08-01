package be.nalebrun.musicroom.ui.screen

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import be.nalebrun.musicroom.ui.element.ActionItem
import be.nalebrun.musicroom.ui.element.ActionSheet
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.FriendsViewModel
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistAccessViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccessUi(playlistId: Int) {
    val viewModel: PlaylistAccessViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val friendsWithAccess by viewModel.friendsWithAccess.collectAsState()
    val friendsWithoutAccess by viewModel.friends.collectAsState()

    LaunchedEffect(playlistId) {
        viewModel.getAccessFriends(playlistId)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            PageTopBackButton(onClick = { navigationViewModel.navigateBack() })
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (friendsWithAccess.isNotEmpty()) {
                    item {
                        Text(
                            "Friends with access :",
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(friendsWithAccess) { request ->
                        ManageAccessCard(request.addresseeName, request.playlistId, request.addresseeId)
                    }
                }

                item {
                    Text(
                        "friends :",
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                items(friendsWithoutAccess) { friend ->
                    AddAccessCard(viewModel, friend.otherUsername, friend.otherId, playlistId)
                }
            }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.NONE,
        )
    }
}

@Composable
fun ManageAccessCard(
    name: String,
    playlistId: Int,
    friendId: Int
) {
    val viewModel: PlaylistAccessViewModel = hiltViewModel()
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
                painter = painterResource(R.drawable.baseline_account_circle_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(text = name, fontWeight = FontWeight.Bold)
        }
        Image(
            painter = painterResource(R.drawable.outline_cancel_24),
            contentDescription = "Remove",
            modifier = Modifier.clickable(true, onClick = {
                viewModel.leavePlaylistAccess(playlistId, friendId)
            })
        )
    }
}

@Composable
fun AddAccessCard(
    accessViewModel: PlaylistAccessViewModel,
    name: String,
    friendId: Int,
    playlistId: Int
) {
    var invitSent by remember { mutableStateOf(false) }

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
                painter = painterResource(R.drawable.baseline_account_circle_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(text = name, fontWeight = FontWeight.Bold)
        }
        Image(
            painter = painterResource(if (invitSent) {
                R.drawable.outline_expand_circle_down_24
            } else {
                R.drawable.outline_add_circle_24
            }),
            contentDescription = "Add",
            modifier = Modifier.clickable(true, onClick = {
                accessViewModel.giveFriendAccessToPlaylist(friendId, playlistId)
                invitSent = true
            })
        )
    }
}
