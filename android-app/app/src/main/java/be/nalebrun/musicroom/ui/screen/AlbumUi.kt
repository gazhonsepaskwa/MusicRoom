package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.AlbumListCard
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.viewmodel.AlbumViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import coil3.compose.AsyncImage

@Composable
fun AlbumUi(albumId: Int) {
    val viewModel: AlbumViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val musics by viewModel.musics.collectAsState()
    val image by viewModel.image.collectAsState()
    val name by viewModel.albumName.collectAsState()

    var shuffleOn by remember { mutableStateOf(false) }

    LaunchedEffect(albumId) {
        viewModel.getAlbum(albumId)
    }

    Column (
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            PageTopBackButton(onClick = { navigationViewModel.navigateBack() })
            Row (
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = 2.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f).height(120.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // album title
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    // play / shuffle
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_play_arrow_24),
                            contentDescription = "",
                            modifier = Modifier
                                .size(35.dp)
                                .clickable(true, onClick = {
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
                            modifier = Modifier
                                .size(35.dp)
                                .clickable(enabled = false, onClick = { /*shuffleOn = !shuffleOn*/ }),
                            tint = if (shuffleOn) MaterialTheme.colorScheme.primary.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize(),
                        model = image,
                        contentDescription = null
                    )
                }
            }
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                items(musics) { item ->
                    AlbumListCard(viewModel.musicRepository, item)
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
                }}
        }

        BottomScreenMenu(
            activeScreen = ActiveScreen.NONE,
        )
    }
}