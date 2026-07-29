package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import be.nalebrun.musicroom.R
import be.nalebrun.musicroom.apiJsonStruct.responds.ForeignDevice
import be.nalebrun.musicroom.viewmodel.DevicesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceControlBottomSheet(
    viewModel: DevicesViewModel,
    onDismissRequest: () -> Unit
) {
    val devices by viewModel.devices.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAvailableDevices()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Control device",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(devices) { device ->
                    DeviceItem(device, {
                        viewModel.askDeviceControl(device)
                    })
                }
            }
        }
    }
}

@Composable
fun DeviceItem(device: ForeignDevice, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_devices_other_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = device.name,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PermissionIcon(enabled = device.canTogglePlayPause, R.drawable.outline_play_arrow_24)
            PermissionIcon(enabled = device.canModifyMusic, R.drawable.outline_queue_music_24)
            PermissionIcon(enabled = device.canSeek, R.drawable.outline_sliders_24)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Image(
                painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .rotate(180f)
            )
        }
    }
}

@Composable
fun PermissionIcon(enabled: Boolean, icon: Int) {
    Image(
        painter = painterResource(id = icon),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        alpha = if (enabled) 1f else 0.5f
    )
}
