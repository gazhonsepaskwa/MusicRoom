package be.nalebrun.musicroom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.ui.screen.AlbumUi
import be.nalebrun.musicroom.ui.screen.ArtistUi
import be.nalebrun.musicroom.ui.screen.AuthUi
import be.nalebrun.musicroom.ui.screen.FriendsUi
import be.nalebrun.musicroom.ui.screen.LibraryUi
import be.nalebrun.musicroom.ui.screen.MusicPlayerUi
import be.nalebrun.musicroom.ui.screen.PlaylistUi
import be.nalebrun.musicroom.ui.screen.SearchUi
import be.nalebrun.musicroom.ui.screen.SettingsUi
import be.nalebrun.musicroom.ui.screen.ProfileUi
import be.nalebrun.musicroom.ui.screen.UserProfileUi
import be.nalebrun.musicroom.ui.screen.ServerSettingsUi
import be.nalebrun.musicroom.ui.screen.ChangePasswordUi
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

/**
 * Function that Create the NavGraph.
 * @see NavHost
 * @param navController    NavController to set up the NavGraph in
 * @param APIRepository   Repository to manage authRequest to the API
 * @param startDestination Name of the route to start the app
 * @author nalebrun
 */
@Composable
fun CreateNavGraph(
    navController:        NavHostController,
    startDestination:     String
) {
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val navigationEvent by navigationViewModel.navigationEvent.observeAsState()
    val backEvent by navigationViewModel.backEvent.observeAsState()

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { route ->
            navController.navigate(route)
            navigationViewModel.clearNavigationEvent()
        }
    }

    LaunchedEffect(backEvent) {
        if (backEvent == true) {
            navController.popBackStack()
            navigationViewModel.clearBackEvent()
        }
    }

    NavHost(
        navController =     navController,
        startDestination =  startDestination,
    ) {
        composable(route = "auth")          { AuthUi() }
        composable(route = "favorite")      { PlaylistUi(-1) }
        composable(route = "library")       { LibraryUi() }
        composable(route = "friends")       { FriendsUi() }
        composable(route = "settings")      { SettingsUi() }
        composable(route = "profile")       { ProfileUi() }
        composable(route = "server-settings") { ServerSettingsUi() }
        composable(route = "change-password") { ChangePasswordUi() }
        composable(route = "music-player")  { MusicPlayerUi() }
        composable(route = "search")        { SearchUi() }
        composable(route = "artist/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
             ArtistUi(artistId = id!!.toInt())
        }
        composable(route = "playlist/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            PlaylistUi(id = id!!.toInt())
        }
        composable(route = "album/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
             AlbumUi(albumId = id!!.toInt())
        }
        composable(route = "user/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1
            UserProfileUi(userId = id)
        }
    }
}
