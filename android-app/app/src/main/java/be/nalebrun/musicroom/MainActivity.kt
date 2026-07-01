package be.nalebrun.musicroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import be.nalebrun.musicroom.ui.theme.MusicRoomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    // Repositories and shared viewModel moved to DI (see hilt DI)

    // main
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContent {
                // shared viewModel
//                navigationViewModel = viewModel<NavigationViewModel>(
//                    factory = NavigationViewModelFactory()
//                )

                MusicRoomTheme() {
                    // late init the navController
                    navController = rememberNavController()

                    // create the app
                    CreateNavGraph(
                        navController =         navController,
                        startDestination =      "auth"
                    )
                }
            }
        } catch (e: Throwable) {
            // Catch Crashes to Get error logs
            Log.e("MainActivity", "CRASH CAUGHT: ${e.javaClass.simpleName}", e)
            e.stackTrace.forEach { Log.e("MainActivity", "  at $it", Throwable()) }
            throw e // Re-throw so it's in logcat
        }
    }
}