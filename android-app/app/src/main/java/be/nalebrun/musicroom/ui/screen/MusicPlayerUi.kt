package be.nalebrun.musicroom.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import coil3.compose.AsyncImage

enum class Repeat{
    NO,
    ONE,
    YES
}

@Preview
@Composable
fun MusicPlayerUi() {

    var songNow by remember { mutableIntStateOf(10004) }
    var songEnd by remember { mutableIntStateOf(23043) }

    var lyrics  by remember { mutableStateOf(""      ) }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PageTopBackButton()

        CoverArt(lyrics)

        // Title and artist
        Column() {
            Text("Someone You Loved", fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text("Lewis Capaldi")
        }

        SongProgressBar()

        MusicControlButtons()

        BottomButtons()
    }
}

@Composable
fun CoverArt(lyrics: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Back Box (Text/Lyrics)
        Box(
            modifier = Modifier
                .offset(x = 50.dp)
                .size(200.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = lyrics,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Right
            )
        }

        // Front Box (Image)
        Box(
            modifier = Modifier
                .offset(x = (-30).dp)
                .size(240.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fm.media-amazon.com%2Fimages%2FI%2F61xp%2Be6A%2BVL._AC_SL1457_.jpg&f=1&nofb=1&ipt=f467e010dd014469c9259c2842f1935c5429b5b4aa3efcf3d3923e1c9ac42116",
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongProgressBar() {
    var songNow by remember { mutableIntStateOf(0) }
    var songEnd by remember { mutableIntStateOf(1000) }

    Column() {
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.Black,
                inactiveTrackColor = Color.Gray
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color.Black, CircleShape)
                        .border(BorderStroke(1.dp, Color.White), CircleShape)
                )
            },
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .border(BorderStroke(1.dp, Color.Black), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(sliderPosition)
                        .height(8.dp)
                        .background(Color.Black, CircleShape)
                        .border(BorderStroke(1.dp, Color.Black), CircleShape)
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(msToReadableString(songNow), fontSize = 10.sp)
            Text(msToReadableString(songEnd), fontSize = 10.sp)
        }
    }
}

fun msToReadableString(msTime: Int) : String {
    val min: Int = (msTime.toFloat() / 60000).toInt()
    val sec: Int = ( (msTime.toFloat() % 60000) / 1000).toInt()
    val str = min.toString() + ":" + sec.toString() + if (sec.toString().length == 1) "0" else ""
    return str
}

@Composable
fun MusicControlButtons() {

    var repeat  by remember { mutableStateOf(Repeat.NO) }
    var shuffle by remember { mutableStateOf(false    ) }
    var play    by remember { mutableStateOf(false    ) }

    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val controlIconsSize: Dp = 40.dp
        Image(
            painter = painterResource(id = when {
                shuffle -> R.drawable.outline_shuffle_on_24
                else -> R.drawable.outline_shuffle_24
            } as Int ),
            contentDescription = "",
            Modifier
                .size(controlIconsSize)
                .clickable(onClick = {
                    shuffle = !shuffle
                })
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_skip_previous_24),
                contentDescription = "",
                Modifier
                    .size(controlIconsSize)
                    .clickable(onClick = {

                    })
            )
            Image(
                painter = painterResource(id = when {
                    play -> R.drawable.outline_pause_24
                    else -> R.drawable.outline_play_arrow_24
                }),
                contentDescription = "",
                Modifier
                    .size(controlIconsSize)
                    .clickable(onClick = {
                        play = !play
                    })
            )
            Image(
                painter = painterResource(id = R.drawable.outline_skip_next_24),
                contentDescription = "",
                Modifier
                    .size(controlIconsSize)
                    .clickable(onClick = {

                    })
            )
        }
        Image(
            painter = painterResource(id = when (repeat) {
                Repeat.NO  -> R.drawable.outline_repeat_24
                Repeat.ONE -> R.drawable.outline_repeat_one_24
                Repeat.YES -> R.drawable.outline_repeat_on_24
            }),
            contentDescription = "",
            Modifier
                .size(controlIconsSize)
                .clickable(onClick = {
                    // go to next state
                    repeat = Repeat.entries.toTypedArray()[(repeat.ordinal + 1) % Repeat.entries.size]
                })
        )
    }
}

@Composable
fun BottomButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Control device Button
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .border(border = BorderStroke(2.dp, Color.Black), shape = CircleShape)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_devices_other_24),
                contentDescription = "",
                Modifier.size(25.dp)
            )
            Text("Control device")
        }

        // musique queue
        Image(
            painter = painterResource(id = R.drawable.outline_devices_other_24),
            contentDescription = "",
            Modifier.size(25.dp)
        )
    }
}
