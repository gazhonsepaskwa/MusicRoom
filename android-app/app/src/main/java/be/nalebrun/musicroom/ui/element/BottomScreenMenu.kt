package be.nalebrun.musicroom.ui.element

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import be.nalebrun.musicroom.viewmodel.SettingsViewModel

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

    val settingsViewModel: SettingsViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val waitingList = musicViewModel.waitingList.collectAsState()
    val debugText = settingsViewModel.debugText.collectAsState()

    Column() {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onBackground)
        if (activity != null && waitingList.value.isNotEmpty()) {
            MiniPlayer(musicViewModel, activity)
        }
        Navigation(activeScreen)
        TextField(
            value = debugText.value,
            onValueChange = { settingsViewModel.updateDebugText(it) },
            label = { Text("debug") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}