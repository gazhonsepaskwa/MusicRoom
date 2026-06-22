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
        try {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            val httpClient = OkHttpClient()
            credentialRepository = CredentialRepository(this)
            authRepository = AuthRepository(httpClient)
            setContent {
                MusicRoomTheme() {
                    navController = rememberNavController()
                    val startDestination: String = "auth"
                    MusicRoomApp(
                        navController = navController,
                        authRepository = authRepository,
                        startDestination = startDestination
                    )
                }
            }
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
    authRepository: AuthRepository,
    startDestination: String
) {
    SetupNavHost(
        navController = navController,
        authRepository = authRepository,
        startDestination = startDestination
    )
}
