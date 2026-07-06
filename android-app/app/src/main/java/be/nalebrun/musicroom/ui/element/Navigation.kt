package be.nalebrun.musicroom.ui.element

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

enum class ActiveScreen{
    FAVORITE,
    LIBRARY,
    FRIENDS,
    SETTINGS,
    NONE
}

@Composable
fun Navigation(
    activeScreen: ActiveScreen,
) {
    // passing the activity scope the view model to the activity
    // level making it share a global instance
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    // Ui
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomScreenMenuButton(
            buttonText = "Favorite",
            buttonIcon = if (activeScreen == ActiveScreen.FAVORITE)
                R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24,
            onClick = {
                if (activeScreen != ActiveScreen.FAVORITE) {
                    navigationViewModel.navigateTo("favorite")
                }
            }
        )
        BottomScreenMenuButton(
            buttonText = "Library",
            buttonIcon = if (activeScreen == ActiveScreen.LIBRARY)
                R.drawable.baseline_library_music_24
                else R.drawable.outline_library_music_24,
            onClick = {
                if (activeScreen != ActiveScreen.LIBRARY) {
                    navigationViewModel.navigateTo("library")
                }
            }
        )
        BottomScreenMenuButton(
            buttonText = "Friends",
            buttonIcon = if (activeScreen == ActiveScreen.FRIENDS)
                R.drawable.baseline_account_circle_24
                else R.drawable.outline_account_circle_24,
            onClick = {
                if (activeScreen != ActiveScreen.FRIENDS) {
                    navigationViewModel.navigateTo("friends")
                }
            }
        )
        BottomScreenMenuButton(
            buttonText = "Settings",
            buttonIcon = if (activeScreen == ActiveScreen.SETTINGS)
                R.drawable.baseline_egg_alt_24
                else R.drawable.outline_egg_alt_24,
            onClick = {
                // No 'if' because will have sub screens so clicking will get back to main settings screen
                navigationViewModel.navigateTo("settings")
            }
        )
    }

}

@Composable
fun BottomScreenMenuButton(buttonText: String, buttonIcon: Int, onClick: () -> Unit) {
    Column(Modifier
        .clickable(onClick = onClick)
        .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(bottom = 3.dp),
            painter = painterResource(id = buttonIcon),
            contentDescription = ""
        )
        Text(buttonText)
    }
}