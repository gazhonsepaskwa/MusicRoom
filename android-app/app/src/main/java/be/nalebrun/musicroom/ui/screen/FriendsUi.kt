package be.nalebrun.musicroom.ui.screen

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.ActionItem
import be.nalebrun.musicroom.ui.element.ActionSheet
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.FriendsViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsUi() {
    val viewModel: FriendsViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val friends by viewModel.friends.collectAsState()
    val friendRequests by viewModel.friendRequests.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var showPermissionsSheet by remember { mutableStateOf(false) }
    var selectedFriendId by remember { mutableStateOf<Int?>(null) }

    var canSeek by remember { mutableStateOf(false) }
    var canTogglePlayPause by remember { mutableStateOf(false) }
    var canModifyMusic by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val permissionsSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "manage account access",
                    icon = R.drawable.outline_account_circle_24,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                                showPermissionsSheet = true
                            }
                        }
                    }
                ),
                ActionItem(
                    label = "See profile",
                    icon = R.drawable.outline_account_circle_24,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            navigationViewModel.navigateTo("user/$selectedFriendId")
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                ),
                ActionItem(
                    label = "remove friend",
                    icon = R.drawable.baseline_favorite_border_24,
                    color = MaterialTheme.colorScheme.error,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            viewModel.removeFriend(selectedFriendId)
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            ),
            sheetState = sheetState
        )
    }

    if (showPermissionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPermissionsSheet = false },
            sheetState = permissionsSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Manage Access",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                PermissionToggle(
                    label = "Can Seek",
                    checked = canSeek,
                    onCheckedChange = {
                        canSeek = it
                        selectedFriendId?.let { id ->
                            viewModel.updatePermissions(id, it, canTogglePlayPause, canModifyMusic)
                        }
                    }
                )
                PermissionToggle(
                    label = "Can Toggle Play/Pause",
                    checked = canTogglePlayPause,
                    onCheckedChange = {
                        canTogglePlayPause = it
                        selectedFriendId?.let { id ->
                            viewModel.updatePermissions(id, canSeek, it, canModifyMusic)
                        }
                    }
                )
                PermissionToggle(
                    label = "Can Modify Music",
                    checked = canModifyMusic,
                    onCheckedChange = {
                        canModifyMusic = it
                        selectedFriendId?.let { id ->
                            viewModel.updatePermissions(id, canSeek, canTogglePlayPause, it)
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
            Title("Friends")
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (friendRequests.isNotEmpty()) {
                    item {
                        Text(
                            "Request :",
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(friendRequests) { request ->
                        FriendRequestCard(
                            name = request.requesterName ?: "Unknown",
                            onAccept = {
                                Log.d("FriendsUi", "Accept clicked for ${request.requesterId}")
                                request.requesterId?.let { viewModel.acceptFriendRequest(it) }
                            },
                            onDecline = {
                                Log.d("FriendsUi", "Decline clicked for ${request.requesterId}")
                                request.requesterId?.let { viewModel.declineFriendRequest(it) }
                            }
                        )
                    }
                }

                item {
                    Text(
                        "friends :",
                        modifier = Modifier.padding(10.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                items(friends) { friend ->
                    FriendCard(
                        title = friend.otherUsername,
                        onClick = {
                            selectedFriendId = friend.otherId
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.FRIENDS,
        )
    }
}

@Composable
fun FriendRequestCard(
    name: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.baseline_account_circle_24),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(text = name, fontWeight = FontWeight.Bold)
        }
        Row {
            Icon(
                painter = painterResource(R.drawable.outline_check_24),
                contentDescription = "Accept",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable { onAccept() },
                tint = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(R.drawable.outline_cancel_24),
                contentDescription = "Decline",
                modifier = Modifier.clickable { onDecline() },
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun FriendCard(
    title: String,
    onClick: () -> Unit = {}
) {
    Row (
        horizontalArrangement = Arrangement
            .spacedBy(10.dp)
        ,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clickable { onClick() }
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            modifier = Modifier
                .padding(bottom = 3.dp)
                .size(24.dp),
            painter = painterResource(R.drawable.baseline_account_circle_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onBackground
        )

        Text(title, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PermissionToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}