package be.nalebrun.musicroom.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistMusicJson
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.PlaylistCard
import be.nalebrun.musicroom.viewmodel.PlaylistViewModel

//TODO: change friends to api call
@Composable
fun FavoriteUi(id: Int) {
    val viewModel: PlaylistViewModel = hiltViewModel()

    val friends: Int? by viewModel.friends.collectAsStateWithLifecycle()
    val title: String? by viewModel.title.collectAsStateWithLifecycle()
    val isPublic: Boolean? by viewModel.isPublic.collectAsStateWithLifecycle()
    val musics: List<PlaylistMusicJson>? by viewModel.musics.collectAsStateWithLifecycle()
    val number = musics?.size ?: 0

    //TODO: change id to real id
    viewModel.getPlaylist(id)

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
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
                Text(
                    text = if (isPublic ?: false) {
                        "public"
                    } else {
                        "private"
                    }, modifier = Modifier.clickable(true, onClick = {
                        viewModel.updatePublicState(id)
//            publicMode = !publicMode
                        // change mode
                    })
                )
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
                    Image(
                        painter = painterResource(R.drawable.artist),
                        contentDescription = ""
                    )
                    Image(
                        painter = painterResource(R.drawable.note_1),
                        contentDescription = ""
                    )
                }
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
                    Text("Manage access →", fontSize = 10.sp, lineHeight = 10.sp)
                }
            }
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        if (musics != null) {
            for (it in musics!!) {
                PlaylistCard(it.music.title, it.music.artists[0].title)
                HorizontalDivider(thickness = 1.dp, color = Color.Black)
            }
        }
        }

        BottomScreenMenu(
            playing = true,
            title = "La fin de nation Glory",
            artist = "Fuze III",
            activeScreen = ActiveScreen.FAVORITE,
        )
    }
}
