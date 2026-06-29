package be.nalebrun.musicroom.ui.element

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(artist, fontSize = 13.sp,)
        }

        // music control
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = "",
                Modifier.size(30.dp)
            )
            Image(
                painter = painterResource(id =
                    if (playing) R.drawable.outline_pause_24
                    else         R.drawable.outline_play_arrow_24
                ),
                contentDescription = "",
                Modifier.size(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = "",
                Modifier.size(30.dp)
            )
        }
    }
}

@Preview
@Composable
fun PrevMiniPlayer() {
    MiniPlayer(playing = true, "La fin de Nation Glory", "Fuze III")
}