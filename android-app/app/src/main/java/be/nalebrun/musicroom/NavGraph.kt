package be.nalebrun.musicroom

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavHost(
    navController: NavHostController,
    authRepository: AuthRepository,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = "auth") {
            PaddingTop({ AuthUi(navController, authRepository) }, 50)
        }
        composable(route = "search") {
            PaddingTop({ SearchUi() }, 50)
        }
    }
}
