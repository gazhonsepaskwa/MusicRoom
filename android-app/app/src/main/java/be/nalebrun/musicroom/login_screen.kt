package be.nalebrun.musicroom

import android.R
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import javax.annotation.processing.Generated

@Composable
fun CustomTextField(title: String, modifier: Modifier = Modifier) {
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
            var text by remember { mutableStateOf("") }

            TextField(
                value = text,
                onValueChange = { text = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                textStyle = TextStyle(fontSize = 13.sp),
                modifier = Modifier
                    .height(45.dp)
            )
        }
    }
}

@Composable
fun Black_White_Button(text : String, modifier: Modifier = Modifier, active : Boolean, onClick: () -> Unit) {
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

@Composable
fun LoginSignInScreenUi() {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var loginMode by remember { mutableStateOf(true) } // true = Login Mode, false = Sign-in Mode
        Text("Music Room", fontSize = 20.sp)
        Text(text = if (loginMode) "Login" else "Sign-in", modifier = Modifier.padding(bottom = 30.dp), fontSize = 30.sp)
        CustomTextField("username or email", Modifier.padding(bottom = 10.dp))
        CustomTextField("password", Modifier.padding(bottom = 30.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.5f.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 5.dp)
        ) {
            val buttonMod = Modifier.height(60.dp)
            Black_White_Button(text = if (loginMode) "Sign-in" else "Login",  modifier = buttonMod.weight(2f), active = false, onClick = {
                loginMode = !loginMode
            })
            Black_White_Button(text = if (loginMode) "Login ->" else "Sign-in ->" , modifier = buttonMod.weight(3f), active = true, onClick = {

            })
        }

    }
}