package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.SearchResponseJson
import be.nalebrun.musicroom.ui.element.ActionItem
import be.nalebrun.musicroom.ui.element.ActionSheet
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.CustomTextField
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.SearchViewModel
import be.nalebrun.musicroom.viewmodel.UserProfileViewModel
import coil3.compose.AsyncImage

enum class ResultType {
    ARTIST,
    MUSIC,
    PLAYLIST,
    ALBUM,
    USER
}

@Composable
fun SearchUi() {
    val viewModel:      SearchViewModel = hiltViewModel()

    val selectedFilters = remember { mutableStateListOf<String>("music", "artist", "album", "playlist", "user") }
    var tfSearch by remember { mutableStateOf("") }
    val results by viewModel.results.collectAsState()

    // search if the search bar is updated or if a filter is edited
    LaunchedEffect(tfSearch) {
        viewModel.search(tfSearch, 0, 10, filters = selectedFilters)
    }
    LaunchedEffect(selectedFilters.size) {
        viewModel.search(tfSearch, 0, 10, filters = selectedFilters)
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // search bar
            CustomTextField("", tfSearch, { tfSearch = it })

            Filters(
                filters = arrayOf("music", "artist", "album", "playlist", "user"),
                selectedFilters = selectedFilters,
                onToggle = { filter ->
                    if (selectedFilters.contains(filter)) {
                        selectedFilters.remove(filter)
                    } else {
                        selectedFilters.add(filter)
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(results) { item ->
                    when (item) {
                        is SearchResponseJson.Music -> {
                            var subtitle : String = ""
                            item.artists.forEach { it ->
                                if (subtitle.isEmpty()) {
                                    subtitle += it.title
                                } else {
                                    subtitle += ", ${it.title}"
                                }
                            }

                            SearchResultCard(
                                id = item.id, // unused
                                music = item.toMusicJson(),
                                resultType = ResultType.MUSIC,
                                title = item.title,
                                subtitle = subtitle
                            )
                        }
                        is SearchResponseJson.Album -> {
                            SearchResultCard(
                                id = item.id,
                                music = null,
                                resultType = ResultType.ALBUM,
                                title = item.title,
                                subtitle = "${item.music.size} songs"
                            )
                        }
                        is SearchResponseJson.Artist -> {
                            SearchResultCard(
                                id = item.id,
                                music = null,
                                resultType = ResultType.ARTIST,
                                title = item.title,
                                subtitle = "${item.albums.size} albums"
                            )
                        }
                        is SearchResponseJson.User -> {
                            SearchResultCard(
                                id = item.id,
                                music = null,
                                resultType = ResultType.USER,
                                title = item.username,
                                subtitle = ""
                            )
                        }
                    }
                }
            }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.SEARCH,
        )
    }
}

/**
 * Component to display a search result
 * @param id ID of the result
 * @param resultType Type of the result
 * @param title First line to display
 * @param subtitle Second line to display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultCard(
    id: Int,
    music: MusicJson?,
    resultType: ResultType,
    title: String,
    subtitle : String,
    navigationViewModel: NavigationViewModel = if (LocalActivity.current != null) {
        hiltViewModel(LocalActivity.current as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    },
    musicViewModel: MusicViewModel = if (LocalActivity.current != null) {
        hiltViewModel(LocalActivity.current as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    },
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    var showActionSheet by remember { mutableStateOf(false) }
    var showFavoriteSlotsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showActionSheet) {
        ActionSheet(
            onDismissRequest = { showActionSheet = false },
            actions = listOf(
                ActionItem(
                    label = "Set as favorite",
                    icon = R.drawable.baseline_edit_24,
                    onClick = {
                        showActionSheet = false
                        showFavoriteSlotsSheet = true
                    }
                ),
                ActionItem(
                    label = "Add to queue (next)",
                    icon = R.drawable.outline_play_arrow_24,
                    onClick = {
                        showActionSheet = false
                        music?.let { musicViewModel.addSongToWaitingListNext(it) }
                    }
                )
            ),
            sheetState = sheetState
        )
    }

    if (showFavoriteSlotsSheet) {
        FavoriteSlotsBottomSheet(
            onDismissRequest = { showFavoriteSlotsSheet = false },
            onSlotSelected = { slotIndex ->
                showFavoriteSlotsSheet = false
                music?.let {
                    userProfileViewModel.updateFavoriteMusic(slotIndex, it.id)
                }
            }
        )
    }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp, end = 10.dp)
            .background(MaterialTheme.colorScheme.background)
            .height(50.dp)
            .fillMaxWidth()
            .clickable(onClick = {
               when(resultType) {
                   // navigate
                   ResultType.ARTIST    -> navigationViewModel.navigateTo("artist/${id}")
                   ResultType.ALBUM     -> navigationViewModel.navigateTo("album/${id}")
                   ResultType.PLAYLIST  -> navigationViewModel.navigateTo("playlist/${id}")
                   // other action
                   ResultType.USER -> navigationViewModel.navigateTo("user/${id}")
                   ResultType.MUSIC -> musicViewModel.addSongToWaitingListNext(music ?: MusicJson(-1))
               }
            })
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                modifier = Modifier
                    .padding(2.dp)
                    .size(36.dp),
                painter = painterResource(id = when(resultType) {
                    ResultType.ARTIST -> R.drawable.artist
                    ResultType.MUSIC -> R.drawable.note_1
                    ResultType.PLAYLIST -> R.drawable.playlist_tmp
                    ResultType.ALBUM -> R.drawable.album
                    ResultType.USER -> R.drawable.baseline_account_circle_24
                }),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Column (
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.basicMarquee())
                if (resultType != ResultType.USER) {
                    Text(subtitle, maxLines = 1, modifier = Modifier.basicMarquee())
                }
            }
        }
        if (resultType == ResultType.MUSIC) {
            Icon(
                painter = painterResource(id = R.drawable.outline_more_horiz_24),
                contentDescription = "More",
                modifier = Modifier
                    .clickable { showActionSheet = true }
                    .padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSlotsBottomSheet(
    onDismissRequest: () -> Unit,
    onSlotSelected: (Int) -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val favoriteMusics by viewModel.favoriteMusics.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile(-1)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Select a slot",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (i in 0 until 3) {
                    val music = favoriteMusics.getOrNull(i)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSlotSelected(i + 1) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (music != null) {
                                AsyncImage(
                                    model = music.album?.images?.firstOrNull() ?: "https://via.placeholder.com/150",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = music?.title ?: "Empty",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Filters
@Composable
fun Filters(
    filters: Array<String>,
    selectedFilters: List<String>,
    onToggle: (String) -> Unit
) {
    Column() {
        filters.toList().chunked(2).forEach { chunk ->
            FilterRow(
                filters = chunk.toTypedArray(),
                selectedFilters = selectedFilters,
                onToggle = onToggle
            )
        }
    }
}

@Composable
fun FilterRow(
    filters : Array<String>,
    selectedFilters: List<String>,
    onToggle: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5f.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 5.dp)
    ) {
        for (filter in filters) {
            FilterButton(
                text = filter,
                modifier = Modifier.weight(1f),
                active = selectedFilters.contains(filter),
                onClick = { onToggle(filter) }
            )
        }
    }
}

@Composable
fun FilterButton(
    text : String,
    modifier: Modifier = Modifier,
    active : Boolean,
    onClick : () -> Unit
) {
    Row(
        modifier = modifier
            .padding(5.dp)
            .clip(shape = RoundedCornerShape(99.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(99.dp)
            )
            .background(color = when (active) {
                true  -> MaterialTheme.colorScheme.onBackground
                false -> MaterialTheme.colorScheme.background
            })
            .height(30.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = when (active) {
                false -> MaterialTheme.colorScheme.onBackground
                true  -> MaterialTheme.colorScheme.background
            },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
