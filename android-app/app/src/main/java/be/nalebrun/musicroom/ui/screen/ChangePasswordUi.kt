package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun ChangePasswordUi() {
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Title("Change Password")
                PageTopBackButton(onClick = {
                    navigationViewModel.navigateTo("settings")
                })
            }
            Column(modifier = Modifier.padding(top = 20.dp)) {
                CustomTextField(title = "Old Password", text = oldPassword, onValueChange = { oldPassword = it })
                CustomTextField(title = "New Password", text = newPassword, onValueChange = { newPassword = it }, modifier = Modifier.padding(top = 10.dp))
                CustomTextField(title = "Confirm New Password", text = confirmPassword, onValueChange = { confirmPassword = it }, modifier = Modifier.padding(top = 10.dp))
                BlackOrWhiteButton(text = "Update Password", active = true, onClick = { /* TODO */ }, modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp))
            }
        }
        BottomScreenMenu(activeScreen = ActiveScreen.SETTINGS)
    }
}
