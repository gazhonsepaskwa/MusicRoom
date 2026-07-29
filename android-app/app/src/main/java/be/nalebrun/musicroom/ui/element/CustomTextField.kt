package be.nalebrun.musicroom.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Text field
 * @param title the name displayed over the text field
 * @param text the text variable
 * @param onValueChange the trigger on change to [text]
 * @param modifier style modifier
 * @param visualTransformation visual transformation for the text field
 * @param singleLine whether the text field is single line. Default to true
 * @author nalebrun
 */
@Composable
fun CustomTextField(
    title: String,
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true
) {
    Column(
        modifier = modifier
    ) {
        Text(text = title, modifier = Modifier.padding(horizontal = 30.dp))
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clip(shape = RoundedCornerShape(99.dp))
                .background(Color.White)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(99.dp)
                )
                .fillMaxWidth()
        ) {
            TextField(
                value = text,
                onValueChange = onValueChange,
                visualTransformation = visualTransformation,
                singleLine = singleLine,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                textStyle = TextStyle(fontSize = 13.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            )
        }
    }
}