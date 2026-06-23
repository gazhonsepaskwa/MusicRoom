package be.nalebrun.musicroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.ui.theme.MusicRoomTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController


    // Repositories
    private lateinit var credentialRepository: CredentialRepository
    private lateinit var APIRepository:       APIRepository


    // main
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            // create the http client for APIs repository
            val httpClient = OkHttpClient()
            // APIs repositories
            APIRepository = APIRepository(httpClient)
            // more soon ...

            credentialRepository = CredentialRepository(this)
            setContent {
                MusicRoomTheme() {
                    // late init the navController
                    navController = rememberNavController()

                    // select start destination
                    val startDestination: String = selectStartDestination("search")

                    // create the app
                    MusicRoomApp(
                        navController = navController,
                        APIRepository = APIRepository,
                        startDestination = startDestination
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

/**
 * Select the Start Screen of the application based on login status.
 * If the user is already logged in, Skipping the AuthUi
 * Else AuthUi
 * @param nonLoginDestination Destination after the login
 * @return The computed result
 * @author nalebrun
 */
fun selectStartDestination(
    nonLoginDestination: String
) : String {
    // TODO implement the Function
    val startDest : String = "auth"
    return startDest
}

/**
 * Entrypoint for Application
 * @author nalebrun
 */
@Composable
fun MusicRoomApp(
    navController: NavHostController,
    APIRepository: APIRepository,
    startDestination: String
) {
    CreateNavGraph(
        navController = navController,
        APIRepository = APIRepository,
        startDestination = startDestination
    )
}
