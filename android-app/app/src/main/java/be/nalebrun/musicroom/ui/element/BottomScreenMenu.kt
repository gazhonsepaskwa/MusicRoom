package be.nalebrun.musicroom.ui.element

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.viewmodel.MusicViewModel

@Composable
fun BottomScreenMenu(
    // var for Navigation
    activeScreen: ActiveScreen
) {

    val activity = LocalActivity.current
    val musicViewModel: MusicViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val waitingList = musicViewModel.waitingList.collectAsState()

    Column() {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
        if (activity != null && waitingList.value.isNotEmpty()) {
            MiniPlayer(musicViewModel, activity)
        }
        Navigation(activeScreen)
    }
}