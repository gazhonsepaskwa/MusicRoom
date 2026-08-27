package be.nalebrun.musicroom.ui.screen

import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.AlbumsArtistJson
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.AlbumCard
import be.nalebrun.musicroom.ui.element.ArtistCard
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.viewmodel.ArtistViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import coil3.compose.AsyncImage

enum class ArtistType {
    SONGS,
    ALBUM
}

@Composable
fun ArtistUi(artistId: Int) {
    val viewModel: ArtistViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val artist: String? by viewModel.artist.collectAsStateWithLifecycle()
    val musics: List<MusicJson> by viewModel.musics.collectAsStateWithLifecycle()
    val albums: List<AlbumsArtistJson> by viewModel.albums.collectAsStateWithLifecycle()
    val image: String? by viewModel.artistImage.collectAsStateWithLifecycle()

    var type by remember { mutableStateOf(ArtistType.SONGS) }
    var shuffleOn by remember { mutableStateOf(false) }

    LaunchedEffect(artistId) {
        viewModel.getAlbumsFromArtist(artistId)
        viewModel.getSongsFromArtist(artistId)
    }

    Column (
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Column(
            modifier = Modifier.weight(1f)
        ) {
            PageTopBackButton(onClick = { navigationViewModel.navigateBack() })
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth()

            )
            {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = image,
                    contentDescription = null
                )
            }
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (artist != "") {
                    Text(artist!!, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                } else {
                    Text("???", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                if (type == ArtistType.SONGS) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween
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
                            painter = painterResource(
                                if (shuffleOn) {
                                    R.drawable.outline_shuffle_on_24
                                } else {
                                    R.drawable.outline_shuffle_24
                                }
                            ),
                            contentDescription = "",
                            modifier = Modifier.clickable(
                                enabled = false,
                                onClick = { /*shuffleOn = !shuffleOn*/ }),
                            tint = if (shuffleOn) MaterialTheme.colorScheme.primary.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                        )

                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 15.dp)
            ) {
                Text(
                    "Songs", fontWeight = if (type == ArtistType.SONGS) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }, modifier = Modifier.clickable(onClick = { type = ArtistType.SONGS })
                )

                Text(
                    "Album", fontWeight = if (type == ArtistType.ALBUM) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    modifier = Modifier.clickable(onClick = { type = ArtistType.ALBUM })
                )
            }
//            HorizontalDivider(thickness = 2.dp, color = Color.Black)
            if (type == ArtistType.ALBUM) {
                LazyColumn(
                    Modifier.weight(1f)
                ) {
                    item {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly

                        ) {
                            albums.forEach { item ->
                                AlbumCard(item.images, item.title, item.id, navigationViewModel)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(musics) { it ->
                        ArtistCard(it)
                        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.NONE,
        )

    }
}

