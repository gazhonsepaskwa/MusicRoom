package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.w3c.dom.Text

@Composable
fun LibraryCard(title: String, songs: Int, duration: Int) {
    val hours = duration / (1000 * 60 * 60)
    val minutes = (duration / (1000 * 60)) % 60
    var time = "${hours}h$minutes"
    if (hours == 0)
        time = "$minutes min"

    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("$songs songs ● $time")
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}
