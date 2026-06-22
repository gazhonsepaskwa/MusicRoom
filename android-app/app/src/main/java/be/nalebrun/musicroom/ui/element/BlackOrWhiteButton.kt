package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Button that can be active or not and change color based on the active status
 * @author nalebrun
 * @param text The Text in the button
 * @param modifier style modifier
 * @param active boolean, active is black, not is white
 * @param onClick callback when button is clicked
 */
@Composable
fun BlackOrWhiteButton(
    text : String,
    modifier: Modifier = Modifier,
    active : Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(5.dp)
            .clip(shape = RoundedCornerShape(99.dp))
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = RoundedCornerShape(99.dp)
            )
            .background(color = when (active) {
                true -> Color.Black
                false -> Color.White
            })
            .height(30.dp)
            .clickable(true, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = when (active) {
                true -> Color.White
                false -> Color.Black
            },
            modifier = Modifier
                .fillMaxWidth())
    }
}

