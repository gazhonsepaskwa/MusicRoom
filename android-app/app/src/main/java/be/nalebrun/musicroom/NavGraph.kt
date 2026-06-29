package be.nalebrun.musicroom

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.ui.screen.AuthUi
import be.nalebrun.musicroom.ui.screen.FavoriteUi
import be.nalebrun.musicroom.ui.screen.FriendsUi
import be.nalebrun.musicroom.ui.screen.LibraryUi
import be.nalebrun.musicroom.ui.screen.SearchUi
import be.nalebrun.musicroom.ui.screen.SettingsUi
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
    navigationViewModel:  NavigationViewModel,
    apiRepository:        APIRepository,
    credentialRepository: CredentialRepository,
    startDestination:     String
) {
    val navigationEvent by navigationViewModel.navigationEvent.observeAsState()

    LaunchedEffect(navigationEvent) {
        Log.d("Debug", "whyyyyyy")
        navigationEvent?.let { route ->
            navController.navigate(route)
            navigationViewModel.clearNavigationEvent()
        }
    }

    NavHost(
        navController =     navController,
        startDestination =  startDestination,
    ) {
//         pass repositories to the Uis but declare ViewModels in them ( except for shared View Models )
        composable(route = "auth")   { AuthUi(apiRepository, credentialRepository, navigationViewModel) }
        composable(route = "favorite") { FavoriteUi(navigationViewModel) }
        composable(route = "library") { LibraryUi(navigationViewModel) }
        composable(route = "friends") { FriendsUi(navigationViewModel) }
        composable(route = "settings") { SettingsUi(navigationViewModel) }
        // composable(route = "search") { SearchUi(navigationViewModel) }
    }
}
