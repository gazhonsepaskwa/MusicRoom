package be.nalebrun.musicroom.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu

@Composable
fun LibraryUi() {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hello", fontWeight = FontWeight.Bold, lineHeight = 10.sp)
                    Text("Bye", fontSize = 10.sp, lineHeight = 10.sp)
                    Row {
                        Image(
                            painter = painterResource(R.drawable.artist),
                            contentDescription = ""
                        )
                        Image(
                            painter = painterResource(R.drawable.note_1),
                            contentDescription = ""
                        )
                    }
                }
//                Image()
                Text("should be image later")
            }
            HorizontalDivider(thickness = 2.dp, color = Color.Black)

        }

        BottomScreenMenu(
            playing = true,
            title = "La fin de nation Glory",
            artist = "Fuze III",
            activeScreen = ActiveScreen.LIBRARY,
        )
        }
}