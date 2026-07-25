package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import coil3.compose.AsyncImage

@Composable
fun AlbumCard(image: List<String>, title: String, id: Int, navigationViewModel: NavigationViewModel) {
    Column(
        modifier = Modifier.size(width = 160.dp, height = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(true, onClick = {
                        navigationViewModel.navigateTo("album/$id")
                    }),
                model = if (image.size >= 2) {
                    image[1]
                } else {
                    ""
                },
                contentDescription = null
            )
        }
        Text(title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}