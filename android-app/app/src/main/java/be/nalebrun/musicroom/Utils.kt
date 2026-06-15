package be.nalebrun.musicroom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Add a padding of [padding].dp to the frame given as parameters.
 * Used to be under the android status bar
 * @param content The composable function
 * @param padding The padding to apply to the composable
 * @author nalebrun
 */
@Composable
fun PaddingTop(content: @Composable () -> Unit, padding: Int) {
    Column(
        modifier = Modifier.padding(top = padding.dp)
    ) {
        content() // <--- Execute the composable passed in
    }
}
