package be.nalebrun.musicroom

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

// data class ...(val ... = ...) to send a data to next screen
@Serializable
object Auth

@Serializable
object Search

@Composable
fun SetupNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Auth> {
            PaddingTop({ AuthUi(navController) }, 50)
        }
        composable<Search> {
            PaddingTop({ SearchUi() }, 50)
        }
    }
}
