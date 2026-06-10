package be.nalebrun.musicroom

import android.R
import android.app.appsearch.SearchResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.ui.theme.MusicRoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicRoomTheme() {
                PaddingTop({ LoginSignInScreenUi() }, 50)
            }
        }
    }
}

@Composable
fun PaddingTop(content: @Composable () -> Unit, padding: Int) {
    """
        Add a padding of [padding].dp to the frame given as parameters
        Used to be under the android status bar
    """.trimIndent()
    Column(
        modifier = Modifier.padding(top = padding.dp)
    ) {
        content() // <--- Execute the composable passed in
    }
}

@Composable
fun SearchResultUi() {
    Column(
        modifier = Modifier
            .padding(top = 50.dp)
            .background(Color.White)
    ) {
        SearchBar()
        SearchResultCard(ResultType.MUSIC, "Scared of the dark", "Em Beihold ● Tales of a failed shapeshifter")
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        SearchResultCard(ResultType.PLAYLIST, "Bestof '26", "nalebrun")
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        SearchResultCard(ResultType.ALBUM, "Tales of a failed shapeshifter", "Em Beihold ● 11 song")
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        SearchResultCard(ResultType.ARTIST, "Em Beihold", "5 albums ● 34 songs")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginSignInScreenUi()
}
