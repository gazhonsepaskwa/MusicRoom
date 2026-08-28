package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.FriendRequestStatus
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistProfileJson
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.FavoriteMusicSquare
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel
import coil3.compose.AsyncImage

@Composable
fun UserProfileUi(userId: Int) {
    val viewModel: UserProfileViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    
    val profile by viewModel.profile.collectAsState()
    val favoriteMusics by viewModel.favoriteMusics.collectAsState()
    val friendRequestState by viewModel.friendRequestState.collectAsState()

    var selectedPlaylistTab by remember { mutableStateOf("owned") }

    LaunchedEffect(userId) {
        viewModel.fetchProfile(userId)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        profile?.let { user ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PageTopBackButton(onClick = { navigationViewModel.navigateBack() })
                Spacer(modifier = Modifier.width(8.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Row(modifier = Modifier.padding(vertical = 16.dp)) {
                        AsyncImage(
                            model = "https://cdn.pfps.gg/pfps/7656-default-17.png",
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .border(
                                    3.dp,
                                    MaterialTheme.colorScheme.onBackground,
                                    RoundedCornerShape(999.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text("username:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(user.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            if (user.email != null) {
                                Text("email:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                                Text(user.email ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user.friends != null) {
                            Surface(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("${user.friends} friends", fontWeight = FontWeight.Bold)
                            }
                        }
                        if (!profile!!.isYou) {
                            Button(
                                onClick = {
                                    if (friendRequestState == null) {
                                        viewModel.sendFriendRequest(userId)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (friendRequestState) {
                                        null -> MaterialTheme.colorScheme.onBackground
                                        else -> MaterialTheme.colorScheme.outline
                                    }
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    when (friendRequestState) {
                                        FriendRequestStatus.PENDING -> "pending"
                                        FriendRequestStatus.NOTVIEWED -> "pending"
                                        FriendRequestStatus.ACCEPTED -> "already friends !"
                                        else -> "send friend request"
                                    }, color = MaterialTheme.colorScheme.background
                                )
                            }
                        }
                    }
                }

                item {
                    if (!favoriteMusics.isEmpty()) {
                        Text(
                            "Favorites",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        favoriteMusics.take(3).forEach { music ->
                            Box(modifier = Modifier.weight(1f)) {
                                FavoriteMusicSquare(music)
                            }
                        }
                        // If there are fewer than 3, add Spacers to maintain alignment
                        repeat(3 - favoriteMusics.take(3).size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Playlists :", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        PlaylistTabSwitcher(
                            selectedTab = selectedPlaylistTab,
                            onTabSelected = { selectedPlaylistTab = it }
                        )
                    }
                }

                val displayPlaylists = if (selectedPlaylistTab == "owned") user.ownedPlaylists else user.invitedPlaylists
                if (displayPlaylists != null) {
                    items(displayPlaylists) { playlist ->
                        PlaylistItem(playlist)
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    item {
                        Text("$user hided they playlists", fontSize = 16.sp)
                    }
                }
                if (displayPlaylists?.isEmpty() ?: false) {
                    item {
                        if (selectedPlaylistTab == "owned") {
                            Text("${user.username} has no playlists", fontSize = 16.sp)
                        } else {
                            Text("${user.username} is invited to no playlists, so sad :C", fontSize = 16.sp)
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        }

        BottomScreenMenu(activeScreen = ActiveScreen.SEARCH)
    }
}

@Composable
fun PlaylistItem(playlist: PlaylistProfileJson) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.images.firstOrNull() ?: "https://via.placeholder.com/60",
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = playlist.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_event_list_24),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(text = formatDuration(playlist.duration), fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.note_1),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(text = "${playlist.musicCount}", fontSize = 14.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun PlaylistTabSwitcher(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .background(if (selectedTab == "owned") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                .clickable { onTabSelected("owned") }
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text("owned", color = if (selectedTab == "owned") MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground)
        }
        Box(
            modifier = Modifier
                .background(if (selectedTab == "invited") MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background)
                .clickable { onTabSelected("invited") }
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text("invited", color = if (selectedTab == "invited") MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground)
        }
    }
}

fun formatDuration(durationSeconds: Long): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    return if (hours > 0) "${hours}h${minutes.toString().padStart(2, '0')}" else "${minutes}min"
}
