package be.nalebrun.musicroom

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.ui.theme.MusicRoomTheme
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController


    // Repositories
    private lateinit var credentialRepository: CredentialRepository
    private lateinit var APIRepository: APIRepository

    // shared viewmodel
    private lateinit var navigationViewModel: NavigationViewModel


    // main
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            // create the http client for APIs repository
            val httpClient = OkHttpClient()
            // Repositories
            APIRepository = APIRepository(httpClient)
            credentialRepository = CredentialRepository(this)

            setContent {

                // shared viewModel
                navigationViewModel = viewModel<NavigationViewModel>(
                    factory = NavigationViewModelFactory()
                )

                MusicRoomTheme() {
                    // late init the navController
                    navController = rememberNavController()

                    // create the app
                    CreateNavGraph(
                        navController =         navController,
                        navigationViewModel =   navigationViewModel,
                        apiRepository =         APIRepository,
                        credentialRepository =  credentialRepository,
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