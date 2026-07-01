package be.nalebrun.musicroom.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.APIRepository
import androidx.lifecycle.viewmodel.compose.viewModel
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.CustomTextField

import be.nalebrun.musicroom.viewmodel.PlaylistViewModel
import be.nalebrun.musicroom.viewmodel.PlaylistViewModelFactory
import okhttp3.Response

@Composable
fun PlaylistUI(
    apiRepository: APIRepository
) {
    val viewModel = viewModel<PlaylistViewModel>(
        factory = PlaylistViewModelFactory(apiRepository)
    )
//    val state: Boolean? by viewModel.publicState.collectAsStateWithLyfecycle()
//    viewModel.getPlaylistInfo(1)
//    val test: Response? by viewModel.test.collectAsStateWithLifecycle()
    val isPublic: Boolean? by viewModel.isPublic.collectAsStateWithLifecycle()
    val title: String? by viewModel.name.collectAsStateWithLifecycle()

    //TODO: turn to val later
    var number = 1
    var friends = 1

    Column(
        Modifier.fillMaxSize().padding(top=20.dp)
            .background(Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(top = 2.dp, start = 10.dp, end = 10.dp, bottom = 2.dp)
                .background(Color.White)
                .fillMaxWidth(),
        ) {
            Column {
                Text(title ?: "", fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                Text("$number songs", fontSize = 10.sp, lineHeight = 10.sp)
            }
            //TODO: change to customText
            Text(
                text = if (isPublic ?: false) {
                "public"
            } else {
                "private"
            }, modifier = Modifier.clickable(true, onClick = {
//            publicMode = !publicMode
                // change mode
            })
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                .background(Color.White)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement
                    .spacedBy(8.dp),
                modifier = Modifier
                    .background(Color.White),
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
    }
//    HorizontalDivider(thickness = 2.dp, color = Color.Black)
}

//TODO: add SongCard here or in ui/element