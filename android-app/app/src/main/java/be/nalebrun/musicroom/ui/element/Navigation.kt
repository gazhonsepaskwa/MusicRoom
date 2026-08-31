package be.nalebrun.musicroom.ui.element

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
    SEARCH,
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
        // like
        BottomScreenMenuButton(
            buttonText = "Favorite",
            buttonIcon = if (activeScreen == ActiveScreen.FAVORITE)
                R.drawable.baseline_favorite_24
                else R.drawable.baseline_favorite_border_24,
            onClick = {
                navigationViewModel.navigateTo("favorite") {
                    popUpTo("favorite") { saveState = true }
                    launchSingleTop = true
                }
            }
        )
        // search
        BottomScreenMenuButton(
            buttonText = "Search",
            buttonIcon = if (activeScreen == ActiveScreen.SEARCH)
                R.drawable.outline_saved_search_24
            else R.drawable.outline_search_24,
            onClick = {
                navigationViewModel.navigateTo("search") {
                    popUpTo("favorite") { saveState = true }
                    launchSingleTop = true
                }
            }
        )
        // library
        BottomScreenMenuButton(
            buttonText = "Library",
            buttonIcon = if (activeScreen == ActiveScreen.LIBRARY)
                R.drawable.baseline_library_music_24
                else R.drawable.outline_library_music_24,
            onClick = {
                navigationViewModel.navigateTo("library") {
                    popUpTo("favorite") { saveState = true }
                    launchSingleTop = true
                }
            }
        )
        // friends page
        BottomScreenMenuButton(
            buttonText = "Friends",
            buttonIcon = if (activeScreen == ActiveScreen.FRIENDS)
                R.drawable.baseline_account_circle_24
                else R.drawable.outline_account_circle_24,
            onClick = {
                navigationViewModel.navigateTo("friends") {
                    popUpTo("favorite") { saveState = true }
                    launchSingleTop = true
                }
            }
        )
        // settings
        BottomScreenMenuButton(
            buttonText = "Settings",
            buttonIcon = if (activeScreen == ActiveScreen.SETTINGS)
                R.drawable.baseline_egg_alt_24
                else R.drawable.outline_egg_alt_24,
            onClick = {
                navigationViewModel.navigateTo("settings") {
                    popUpTo("favorite") { saveState = true }
                    launchSingleTop = true
                }
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
        Icon(
            modifier = Modifier
                .padding(bottom = 3.dp)
                .size(24.dp),
            painter = painterResource(id = buttonIcon),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = buttonText,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}