package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistCard(title: String, artist: String) {
    var showbottomSheet by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    if (showbottomSheet) {
        ActionSheet(
            onDismissRequest = { showbottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "add to playlist",
                    icon = R.drawable.playlist_tmp,
                    onClick = {}
                ),
                ActionItem(
                    label = "add to queue",
                    icon = R.drawable.playlist_tmp,
                    onClick = {}
                )
            ),
            sheetState = sheetState
        )
    }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 2.dp, bottom = 2.dp, start = 10.dp, end = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(artist)
        }
        Icon(
            painter = painterResource(R.drawable.menu),
            contentDescription = "",
            modifier = Modifier.clickable(true, onClick = { showbottomSheet = true }),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}