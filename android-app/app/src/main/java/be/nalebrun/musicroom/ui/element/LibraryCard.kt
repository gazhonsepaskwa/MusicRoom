package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import org.w3c.dom.Text

@Composable
fun LibraryCard(title: String, songs: Int, duration: Int) {
    Column {
        Text(title, fontWeight = FontWeight.Bold)
        Text("$songs songs ● $duration")
    }
}
