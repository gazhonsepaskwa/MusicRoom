package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.R

@Composable
fun ArtistCard(title: String, artist: String) {
    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp, end = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(artist)
        }
        Image(
            painter = painterResource(R.drawable.menu),
            contentDescription = ""
        )
    }
}