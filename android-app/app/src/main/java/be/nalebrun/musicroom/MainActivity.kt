package be.nalebrun.musicroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.ui.theme.MusicRoomTheme
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {

    private lateinit var credentialRepository: CredentialRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate called")
        try {
            Log.d("MainActivity", "Calling enableEdgeToEdge")
            enableEdgeToEdge()

            Log.d("MainActivity", "Calling super.onCreate")
            super.onCreate(savedInstanceState)

            Log.d("MainActivity", "Creating OkHttpClient")
            val httpClient = OkHttpClient()

            Log.d("MainActivity", "Creating CredentialRepository")
            credentialRepository = CredentialRepository(this)

            Log.d("MainActivity", "Creating AuthRepository")
            authRepository = AuthRepository(httpClient)

            Log.d("MainActivity", "Calling setContent")
            setContent {
                Log.d("MainActivity", "Inside setContent")
                MusicRoomTheme() {
                    Log.d("MainActivity", "Inside MusicRoomTheme")
                    navController = rememberNavController()
                    Log.d("MainActivity", "NavController created: $navController")
                    val startDestination: String = "Auth"
                    Log.d("MainActivity", "Calling MusicRoomApp")
                    MusicRoomApp(
                        navController = navController,
                        startDestination = startDestination
                    )
                    Log.d("MainActivity", "MusicRoomApp returned")
                }
                Log.d("MainActivity", "setContent block finished")
            }
            Log.d("MainActivity", "onCreate completed successfully")
        } catch (e: Throwable) {
            Log.e("MainActivity", "CRASH CAUGHT: ${e.javaClass.simpleName}", e)
            e.stackTrace.forEach { Log.e("MainActivity", "  at $it", Throwable()) }
            throw e // Re-throw so we can see it in logcat
        }
    }
}

@Composable
fun MusicRoomApp(
    navController: NavHostController,
    startDestination: String
) {
    SetupNavHost(
        navController = navController,
        startDestination = startDestination
    )
}
