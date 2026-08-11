package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.AuthViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

@Composable
fun SettingsUi() {
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val authViewModel: AuthViewModel = hiltViewModel()

    val logoutComplete = authViewModel.logoutComplete.collectAsStateWithLifecycle()

    LaunchedEffect (logoutComplete.value) {
        if (logoutComplete.value) {
            navigationViewModel.navigateTo("auth")
            authViewModel.resetLogoutComplete()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Title("Settings")
            SettingItem("my profile")        { navigationViewModel.navigateTo("profile") }
            SettingItem("server settings")   { navigationViewModel.navigateTo("server-settings") }
            SettingItem("change password")   { navigationViewModel.navigateTo("change-password") }
            SettingItem("logout")            { authViewModel.logout() }

            Text("danger zone", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))
            SettingItem("delete my account") { navigationViewModel.navigateTo("delete-account") }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.SETTINGS,
        )
    }
}

@Composable
fun SettingItem(text: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
