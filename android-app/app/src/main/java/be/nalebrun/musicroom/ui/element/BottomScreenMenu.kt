package be.nalebrun.musicroom.ui.element

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun BottomScreenMenu(
    // var for Navigation
    activeScreen: ActiveScreen
) {
    Column() {
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        MiniPlayer()
        Navigation(activeScreen)
    }
}