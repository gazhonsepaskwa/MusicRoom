package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.CustomTextField
import be.nalebrun.musicroom.ui.element.PageTopBackButton
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.SettingsViewModel

@Composable
fun DeleteAccountUi() {
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val settingsViewModel: SettingsViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxHeight(),
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Title("Delete Account")
            PageTopBackButton(onClick = { navigationViewModel.navigateBack() })
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            CustomTextField(
                title = "confirm password",
                text = password,
                onValueChange = { password = it },
                visualTransformation = PasswordVisualTransformation()
            )

            BlackOrWhiteButton(
                text = "Delete account",
                active = true,
                onClick = {
                    settingsViewModel.deleteAccount(password) {
                        navigationViewModel.navigateTo("auth")
                    }
                },
                modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp)
            )
        }
    }
}
