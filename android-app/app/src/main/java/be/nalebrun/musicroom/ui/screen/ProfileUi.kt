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
import androidx.compose.ui.graphics.Color
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
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistProfileJson
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel
import coil3.compose.AsyncImage

@Composable
fun ProfileUi() {
    val viewModel: UserProfileViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val profile by viewModel.profile.collectAsState()
    val favoriteMusics by viewModel.favoriteMusics.collectAsState()

    var selectedPlaylistTab by remember { mutableStateOf("owned") }

    LaunchedEffect(Unit) {
        println("coucou")
        viewModel.fetchProfile(-1)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                //set profile to mofify mode where people can select
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = when (friendRequestState) {
                null -> Color.Black
                else -> Color.Gray
            }),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(when (friendRequestState) {
                FriendRequestStatus.PENDING -> "pending"
                FriendRequestStatus.NOTVIEWED -> "pending"
                FriendRequestStatus.ACCEPTED -> "already friends !"
                else -> "send friend request" }, color = Color.White)
        }
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
                                .border(3.dp, Color.Black, RoundedCornerShape(999.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text("username:", fontSize = 12.sp, color = Color.Gray)
                            Text(user.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("email:", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                            Text(user.email ?: "N/A", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("${user.friends} friends", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Text(
                        "Favorites",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }

                items(favoriteMusics) { music ->
                    FavoriteMusicItem(music)
                }

                item {
                    Row(
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp).fillMaxWidth(),
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
                }
            }
        } ?: Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Black)
        }

        BottomScreenMenu(activeScreen = ActiveScreen.SEARCH)
    }
}
