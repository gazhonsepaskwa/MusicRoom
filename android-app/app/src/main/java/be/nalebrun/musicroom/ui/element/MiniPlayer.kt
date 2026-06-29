package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nalebrun.musicroom.R

@Composable
fun MiniPlayer(playing: Boolean, title: String, artist: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // song info
        Column(
        ) {
            Text(title)
            Text(artist, fontSize = 10.sp, color = Color.Gray)
        }

        // music control
        Row(
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = ""
            )
            Image(
                painter = painterResource(id =
                    if (playing) R.drawable.outline_pause_24
                    else         R.drawable.outline_play_arrow_24
                ),
                contentDescription = ""
            )
            Image(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = ""
            )
        }
    }
}

//import androidx.compose.ui.tooling.preview.Preview
//@Preview
//@Composable
//fun PrevMiniPlayer() {
//    MiniPlayer(playing = true, "La fin de Nation Glory", "Fuze III")
//}