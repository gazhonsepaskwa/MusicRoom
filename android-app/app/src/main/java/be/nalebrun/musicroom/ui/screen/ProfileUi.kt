package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.EmptyFavoriteMusicSquare
import be.nalebrun.musicroom.ui.element.FavoriteMusicSquare
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

    var isEditingUsername by rememberSaveable {
        mutableStateOf(false)
    }
    var selectedPlaylistTab by remember { mutableStateOf("owned") }

    LaunchedEffect(Unit) {
        viewModel.fetchProfile(-1)
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
                                .border(3.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(999.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text("username:", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            Text(user.username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("email:", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp))
                                Spacer(modifier = Modifier
                                    .width(8.dp))
                                Icon(
                                    painter = painterResource(id = when {
                                        profile!!.showAddress == "PUBLIC" -> R.drawable.outline_visibility_24
                                        profile!!.showAddress == "FRIEND" -> R.drawable.outline_connect_without_contact_24
                                        else -> R.drawable.outline_visibility_off_24
                                    } as Int ),
                                    contentDescription = "",
                                    Modifier
                                        .size(22.dp)
                                        .padding(top = 4.dp)
                                        .clickable(onClick = {
                                            viewModel.changeVisibility("showAddress", profile!!.showAddress!!)
                                        }),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
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
                                .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("${user.friends} friends", fontWeight = FontWeight.Bold)
                        }
                        Icon(
                            painter = painterResource(id = when {
                                profile!!.showFriends == "PUBLIC" -> R.drawable.outline_visibility_24
                                profile!!.showFriends == "FRIEND" -> R.drawable.outline_connect_without_contact_24
                                else -> R.drawable.outline_visibility_off_24
                            } as Int ),
                            contentDescription = "",
                            Modifier
                                .size(22.dp)
                                .clickable(onClick = {
                                    viewModel.changeVisibility("showFriends", profile!!.showFriends!!)
                                }),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Favorites",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 10.dp)

                        )
                        Icon(
                            painter = painterResource(
                                id = when {
                                    profile!!.showPreferedMusics == "PUBLIC" -> R.drawable.outline_visibility_24
                                    profile!!.showPreferedMusics == "FRIEND" -> R.drawable.outline_connect_without_contact_24
                                    else -> R.drawable.outline_visibility_off_24
                                } as Int
                            ),
                            contentDescription = "",
                            Modifier
                                .size(22.dp)
                                .clickable(onClick = {
                                    viewModel.changeVisibility(
                                        "showPreferedMusics",
                                        profile!!.showPreferedMusics!!
                                    )
                                }),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (i in 0 until 3) {
                            val music = favoriteMusics.getOrNull(i)
                            Box(modifier = Modifier.weight(1f)) {
                                if (music != null) {
                                    FavoriteMusicSquare(music, onClick = { navigationViewModel.navigateTo("search") })
                                } else {
                                    EmptyFavoriteMusicSquare(onClick = { navigationViewModel.navigateTo("search") })
                                }
                            }
                        }
                    }
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
                        val visibilityValue = if (selectedPlaylistTab == "owned") profile!!.showCreatedPlaylist else profile!!.showInvitedPlaylist
                        val paramJson =  if (selectedPlaylistTab == "owned") "showCreatedPlaylist" else "showInvitedPlaylist"
                        Icon(
                            painter = painterResource(
                                id = when {
                                    visibilityValue == "PUBLIC" -> R.drawable.outline_visibility_24
                                    visibilityValue == "FRIEND" -> R.drawable.outline_connect_without_contact_24
                                    else -> R.drawable.outline_visibility_off_24
                                } as Int
                            ),
                            contentDescription = "",
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(onClick = {
                                    viewModel.changeVisibility(
                                        paramJson,
                                        visibilityValue!!
                                    )
                                }),
                            tint = MaterialTheme.colorScheme.onSurface
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
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        BottomScreenMenu(activeScreen = ActiveScreen.SEARCH)
    }
}
