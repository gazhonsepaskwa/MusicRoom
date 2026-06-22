package be.nalebrun.musicroom

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import be.nalebrun.musicroom.ui.screen.AuthUi
import be.nalebrun.musicroom.ui.screen.SearchUi

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
    navController:      NavHostController,
    APIRepository:     APIRepository,
    startDestination:   String
) {
    NavHost(
        navController =     navController,
        startDestination =  startDestination
    ) {
        composable(route = "auth")   { AuthUi(navController, APIRepository) }
        composable(route = "search") { SearchUi() }
    }
}
