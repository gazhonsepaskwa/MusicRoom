package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.libraryJson
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.LibraryCard
import be.nalebrun.musicroom.viewmodel.LibraryViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

@Composable
fun LibraryUi() {
    val viewModel: LibraryViewModel = hiltViewModel()
    val activity = LocalActivity.current
    val navigationView: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }

    val playlists: List<libraryJson>? by viewModel.playlists.collectAsStateWithLifecycle()

    viewModel.getPlaylists()


    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text("Library", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 25.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp))
            HorizontalDivider(thickness = 2.dp, color = Color.Black)
            if (playlists != null) {
                for (it in playlists) {
                    LibraryCard(it.title, it.songs, it.duration)
                }
            }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.LIBRARY,
        )
        }
}