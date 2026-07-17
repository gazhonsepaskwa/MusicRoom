package be.nalebrun.musicroom.ui.element

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    musicViewModel: MusicViewModel,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
) {
    val waitingList by musicViewModel.waitingList.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            items(waitingList) { music ->
                WaitingListElem(music)
            }
        }
    }
}

@Composable
fun WaitingListElem(music: MusicJson) {
    Column(
        modifier = Modifier
            .padding(bottom = 5.dp)
    ) {
        // title
        if (music.title.isNotEmpty())   { Text(music.title) }
        else                            { Text("Unknown Title") }

        // artist
        if (music.artists.isNotEmpty()) {
            Text(music.artists.firstOrNull()?.title ?: "Unknown Artist")
        }
        else { Text("Unknown Artist") }

        // separator
        HorizontalDivider(thickness = 0.5f.dp, color = Color.Gray, modifier = Modifier.padding(top = 5.dp))
    }
}