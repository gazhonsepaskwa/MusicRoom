package be.nalebrun.musicroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchUi() {
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