package be.nalebrun.musicroom.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

@Composable
fun LibraryUi(navigationViewModel: NavigationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text("TODO : Library page")
        }
        BottomScreenMenu(
            playing = true,
            title = "La fin de nation Glory",
            artist = "Fuze III",
            activeScreen = ActiveScreen.LIBRARY,
            navigationViewModel = navigationViewModel
        )
    }
}