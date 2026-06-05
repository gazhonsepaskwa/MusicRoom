package be.nalebrun.musicroom

import android.app.appsearch.SearchResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.Image

enum class ResultType {
    ARTIST,
    MUSIC,
    PLAYLIST,
    ALBUM
}

/**
 * Component to display a search result
 * @param resultType Type of the result
 * @param title First line to display
 * @param subtitle Second line to display
 */
@Composable
fun SearchResultCard(resultType: ResultType, title: String, subtitle : String) {
    Row (
        horizontalArrangement = Arrangement
            .spacedBy(10.dp)
        ,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp)
            .background(Color.White)
            .height(50.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
//        AsyncImage(
//            modifier = Modifier.padding(5.dp),
//            model = "https://i.scdn.co/image/ab67616d0000b273dd3a17393ca3f47e4c523c26",
//            contentDescription = null,
//        )
        Image(
            modifier = Modifier
                .padding(2.dp),
            painter = painterResource(id = when(resultType) {
                ResultType.ARTIST -> R.drawable.artist
                ResultType.MUSIC -> R.drawable.note_1
                ResultType.PLAYLIST -> R.drawable.playlist_tmp
                ResultType.ALBUM -> R.drawable.album
            }),
            contentDescription = ""
        )
        Column (
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle)
        }
    }
}