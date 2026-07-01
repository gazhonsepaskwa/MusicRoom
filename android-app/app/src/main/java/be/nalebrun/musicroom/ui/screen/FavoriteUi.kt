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
fun FavoriteUi() {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val friends = 1
        val number = 0
        val isPublic = false
        val title = ""
        Column() {
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
        }
        BottomScreenMenu(
            playing = true,
            title = "La fin de nation Glory",
            artist = "Fuze III",
            activeScreen = ActiveScreen.FAVORITE,
        )
    }
}