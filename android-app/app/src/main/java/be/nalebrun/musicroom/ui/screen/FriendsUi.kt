package be.nalebrun.musicroom.ui.screen

import android.R.attr.contentDescription
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableOpenTarget
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import be.nalebrun.musicroom.ui.element.ActionItem
import be.nalebrun.musicroom.ui.element.ActionSheet
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.ui.element.ActiveScreen
import be.nalebrun.musicroom.ui.element.BottomScreenMenu
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.FriendsViewModel
import be.nalebrun.musicroom.viewmodel.MusicViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsUi() {
    val viewModel: FriendsViewModel = hiltViewModel()
    val friends by viewModel.friends.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedFriendId by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "start control",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                ),
                ActionItem(
                    label = "manage account access",
                    icon = R.drawable.outline_account_circle_24,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                ),
                ActionItem(
                    label = "remove friend",
                    icon = R.drawable.baseline_favorite_border_24,
                    color = Color.Red,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                )
            ),
            sheetState = sheetState
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Title("Friends")
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(friends) { friend ->
                    FriendCard(
                        title = friend.otherUsername,
                        onClick = {
                            selectedFriendId = friend.otherId
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
        BottomScreenMenu(
            activeScreen = ActiveScreen.FRIENDS,
        )
    }
}

@Composable
fun FriendCard(
    title: String,
    onClick: () -> Unit = {}
) {
    Row (
        horizontalArrangement = Arrangement
            .spacedBy(10.dp)
        ,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp)
            .background(Color.White)
            .height(50.dp)
            .fillMaxWidth()
            .clickable { onClick() }
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.padding(bottom = 3.dp),
            painter = painterResource(R.drawable.baseline_account_circle_24),
            contentDescription = ""
        )

        Text(title, fontWeight = FontWeight.Bold)
    }
}