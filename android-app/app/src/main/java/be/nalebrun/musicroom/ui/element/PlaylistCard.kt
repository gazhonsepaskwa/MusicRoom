package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun PlaylistCard(title: String, artist: String) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ActionSheet(
            onDismissRequest = { showBottomSheet = false },
            actions = listOf(
                ActionItem(
                    label = "remove",
                    icon = R.drawable.outline_devices_other_24,
                    onClick = {}
                ),
                ActionItem(
                    label = "add to queue",
                    icon = R.drawable.outline_devices_other_24,
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
        Image(
            modifier = Modifier.clickable(true, onClick = {
                showBottomSheet = true
            }),
            painter = painterResource(R.drawable.menu),
            contentDescription = ""
        )
    }
}