package be.nalebrun.musicroom

import android.app.appsearch.SearchResult
import android.text.Layout
import android.text.method.TextKeyListener
import android.view.RoundedCorner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import coil3.Image
import java.util.logging.Filter

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
@Composable
fun Filters(filters: Array<String>) {
    Column() {
        filters.toList().chunked(2).forEach { chunk ->
            FilterRow(filters = chunk.toTypedArray())
        }
    }
}

@Composable
fun FilterRow(filters : Array<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.5f.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 5.dp)
    ) {
        for (filter in filters) {
            FilterButton(text = filter, modifier = Modifier.weight(1f), active = false)
        }
    }
}

@Composable
fun FilterButton(text : String, modifier: Modifier = Modifier, active : Boolean) {
    Row(
        modifier = modifier
            .padding(5.dp)
            .clip(shape = RoundedCornerShape(99.dp))
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(99.dp)
            )
            .background(color = when (active) {
                true -> Color.Black
                false -> Color.White
            })
            .height(30.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = when (active) {
                true -> Color.White
                false -> Color.Black
            },
            modifier = Modifier
                .fillMaxWidth())
    }

}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clip(shape = RoundedCornerShape(99.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(99.dp)
            )
            .fillMaxWidth()
    ) {
        var text by remember { mutableStateOf("") }

        TextField(
            value = text,
            onValueChange = { text = it },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 13.sp),
            modifier = Modifier
                .height(45.dp)
        )
    }
    Filters(arrayOf("Artist", "Music", "Album", "Playlist", "User"))
}