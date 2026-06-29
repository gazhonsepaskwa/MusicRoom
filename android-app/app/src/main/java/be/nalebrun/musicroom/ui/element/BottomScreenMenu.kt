package be.nalebrun.musicroom.ui.element

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

@Composable
fun BottomScreenMenu(
    // MiniPlayer var
    playing: Boolean,
    title: String,
    artist: String,
    // var for Navigation
    activeScreen: ActiveScreen,
    navigationViewModel: NavigationViewModel
) {
    Column() {
        HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(bottom = 5.dp))
        MiniPlayer(playing = playing, title = title, artist = artist)
        Navigation(activeScreen, navigationViewModel)
    }
}