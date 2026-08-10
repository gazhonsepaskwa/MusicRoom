package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.CustomTextField
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.SettingsViewModel

@Composable
fun ServerSettingsUi() {
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val savedServerUrl by settingsViewModel.serverUrl.collectAsState()

    var serverUrl by remember(savedServerUrl) { mutableStateOf(savedServerUrl) }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Title("Server Settings")
                PageTopBackButton(onClick = { navigationViewModel.navigateBack() })
            }
            Column(modifier = Modifier.padding(top = 20.dp)) {
                CustomTextField(title = "Server URL", text = serverUrl, onValueChange = { serverUrl = it })
                BlackOrWhiteButton(
                    text = "Connect",
                    active = true,
                    onClick = {
                        settingsViewModel.ifServerUrlValid(serverUrl) {
                            settingsViewModel.updateServerUrl(serverUrl) {
                                navigationViewModel.navigateTo("auth")
                            }
                            if (serverUrl == savedServerUrl) {
                                navigationViewModel.navigateTo("settings")
                            }
                        }

                    },
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp)
                )
            }
        }
        BottomScreenMenu(activeScreen = ActiveScreen.SETTINGS)
    }
}
