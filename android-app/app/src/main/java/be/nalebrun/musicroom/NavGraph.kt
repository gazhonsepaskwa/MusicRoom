package be.nalebrun.musicroom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import be.nalebrun.musicroom.viewmodel.AuthViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel
import be.nalebrun.musicroom.viewmodel.NavEvent
import be.nalebrun.musicroom.viewmodel.SocketViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.navigation.navDeepLink

/**
 * Function that Create the NavGraph.
 * @see NavHost
 * @param navController    NavController to set up the NavGraph in
 * @param APIRepository   Repository to manage authRequest to the API
 * @param startDestination Name of the route to start the app
 * @author nalebrun
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNavGraph(
    navController:        NavHostController,
    startDestination:     String
) {
    val navigationViewModel: NavigationViewModel = hiltViewModel()
    val socketViewModel: SocketViewModel = hiltViewModel()
    val navigationEvent by navigationViewModel.navigationEvent.observeAsState()
    val backEvent by navigationViewModel.backEvent.observeAsState()
    val incomingRequest by socketViewModel.incomingRequest.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val messageEvent by navigationViewModel.messageEvent.observeAsState()

    LaunchedEffect(incomingRequest) {
        Log.d("NavGraph", "incomingRequest state changed: $incomingRequest")
    }

    if (incomingRequest != null) {
        ModalBottomSheet(
            onDismissRequest = {
                Log.d("NavGraph", "Dismissing Sheet")
                socketViewModel.answerRequest(false)
            },
            sheetState = sheetState
        ) {
            Log.d("NavGraph", "Rendering BottomSheet Content")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Connection Request",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Device '${incomingRequest?.deviceId}' (User ID: ${incomingRequest?.userId}) wants to connect to your device. Do you accept?",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { socketViewModel.answerRequest(false) }) {
                        Text("No")
                    }
                    Button(onClick = { socketViewModel.answerRequest(true) }) {
                        Text("Yes")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    /**
     * Listen to the navigation event and navigate to the destination
     * if the route is "auth", clear the backstack and navigate to auth
     * @see NavEvent
     */
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            if (event.route == "auth") {
                // Nuclear option: clear everything and go to auth
                navController.navigate("auth") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            } else if (event.builder != null) {
                navController.navigate(event.route, event.builder)
            } else {
                navController.navigate(event.route)
            }
            navigationViewModel.clearNavigationEvent()
        }
    }

    LaunchedEffect(backEvent) {
        if (backEvent == true) {
            navController.popBackStack()
            navigationViewModel.clearBackEvent()
        }
    }

    LaunchedEffect(messageEvent) {
        messageEvent?.let { message ->
            snackbarHostState.showSnackbar(message)
            navigationViewModel.clearMessageEvent()
        }
    }

    // Scaffold needed to display the snackbar and manage its options
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            ///////////////
            // deep link //
            /////////////// aka: from the outside

        // Validate email
        composable(
            route = "validate_email?token={token}",
            deepLinks = listOf(navDeepLink { uriPattern = "musicroom://auth/callback?token={token}" })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token")
            val authViewModel: AuthViewModel = hiltViewModel()

            LaunchedEffect(token) {
                token?.let {
                    Log.d("NavGraph", "Storing token from deep link (validate_email): $it")
                    authViewModel.credentialRepository.setJWT(it)
                    // connect to the socket ? I am not sure if it I should do it there (TODO : check)
                    socketViewModel.connectSocket()
                }
            }
            navController.navigate("search") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }

        ////////////////
        // in app nav //
        ////////////////

        composable(route = "auth")            { AuthUi() }
        composable(route = "favorite")        { PlaylistUi(-1) }
        composable(route = "library")         { LibraryUi() }
        composable(route = "friends")         { FriendsUi() }
        composable(route = "settings")        { SettingsUi() }
        composable(route = "profile")         { ProfileUi() }
        composable(route = "server-settings") { ServerSettingsUi() }
        composable(route = "change-password") { ChangePasswordUi() }
        composable(route = "music-player")    { MusicPlayerUi() }
        composable(route = "search")          { SearchUi() }
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
}
