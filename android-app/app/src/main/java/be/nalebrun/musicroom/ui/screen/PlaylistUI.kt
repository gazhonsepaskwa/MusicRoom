package be.nalebrun.musicroom.ui.screen

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistMusicJson
import be.nalebrun.musicroom.ui.element.ActionItem
import be.nalebrun.musicroom.ui.element.ActionSheet
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.PlaylistCard
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistViewModel

@Composable
fun PlaylistUi(id: Int, owned: Boolean = true) {
    val viewModel: PlaylistViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationView: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val friends: Int? by viewModel.friends.collectAsState()
    val playlistId: Int by viewModel.id.collectAsState()
    val title: String? by viewModel.title.collectAsState()
    val isDefault: Boolean by viewModel.isDefault.collectAsState()
    val isPublic: Boolean? by viewModel.isPublic.collectAsStateWithLifecycle()
    val musics: List<MusicJson> by viewModel.musics.collectAsState()
    val number = musics.size

    var shuffleOn by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (id == -1) {
            viewModel.getFavorite()
        } else {
            viewModel.getPlaylist(id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 2.dp, start = 10.dp, end = 10.dp, bottom = 2.dp)
                    .fillMaxWidth(),
            ) {
                Column {
                    Text(title ?: "", fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                    Text("$number songs", fontSize = 10.sp, lineHeight = 10.sp)
                }
                if (!isDefault && owned) {
                    Row(
                        modifier = Modifier
                            .clickable(true, onClick = {
                                viewModel.updatePublicState(playlistId)
                            }),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isPublic ?: false) {
                                "public"
                            } else {
                                "private"
                            }
                        )
                        Icon(
                            painter = painterResource(
                                if (isPublic ?: false) {
                                    R.drawable.outline_visibility_24
                                } else {
                                    R.drawable.outline_visibility_off_24
                                }
                            ),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement
                        .spacedBy(8.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_play_arrow_24),
                        contentDescription = "",
                        modifier = Modifier.clickable(true, onClick = {
                            viewModel.musicRepository.replaceWaitingList(musics)
                        }),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        painter = painterResource(if (shuffleOn) { R.drawable.outline_shuffle_on_24}
                        else {
                            R.drawable.outline_shuffle_24
                        }),
                        contentDescription = "",
                        modifier = Modifier.clickable(true, onClick = { shuffleOn = !shuffleOn }),
                        tint = if (shuffleOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
                if (!isDefault && owned) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.clickable(true, onClick = {})
                    ) {
                        Text(
                            "Shared with $friends friends",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            lineHeight = 10.sp
                        )
                        Text(
                            text = "Manage access →",
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            modifier = Modifier.clickable(true, onClick = {
                                navigationView.navigateTo("playlist/$id/access")
                            }))
                    }
                }
            }
        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(musics) { item ->
                PlaylistCard(playlistId, item)
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
            }}
        }

        BottomScreenMenu(
            activeScreen = if(isDefault) { ActiveScreen.FAVORITE } else { ActiveScreen.NONE },
        )
    }
}
