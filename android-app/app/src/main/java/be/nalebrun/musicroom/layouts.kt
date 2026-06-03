package be.nalebrun.musicroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ArtistCard() {
    Row (
        horizontalArrangement = Arrangement
            .spacedBy(10.dp)
        ,
        modifier = Modifier
            .padding(top = 30.dp)
            .background(Color.White)
            .height(40.dp)
            .fillMaxWidth()
    ){
        AsyncImage(
            model = "https://i.scdn.co/image/ab67616d0000b273dd3a17393ca3f47e4c523c26",
            contentDescription = null,
        )
        Column (
        ) {
            Text("Renaud")
            Text("x albums")
        }
    }
}