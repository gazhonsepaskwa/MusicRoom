package be.nalebrun.musicroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.nalebrun.musicroom.ui.theme.MusicRoomTheme
import be.nalebrun.musicroom.uiElement.BlackOrWhiteButton
import be.nalebrun.musicroom.uiElement.CustomTextField
import okhttp3.FormBody

class AuthActivity : ComponentActivity() {

    val api = Api()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicRoomTheme() {
                PaddingTop({ AuthUi() }, 50)
            }
        }
    }

    @Composable
    fun AuthUi() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var loginMode by remember { mutableStateOf(true) } // true = Login Mode, false = Sign-in Mode
            var tvResponse by remember { mutableStateOf("") } // empty by default, filled with message when api respond

            // text field variables
            var tfUsername by remember { mutableStateOf("") }
            var tfEmail    by remember { mutableStateOf("") }
            var tfPassword by remember { mutableStateOf("") }

            // Titles
            Text("Music Room", fontSize = 20.sp)
            Text(text = if (loginMode) "Login" else "Sign-in", modifier = Modifier.padding(bottom = 30.dp), fontSize = 30.sp)

            // text fields
            if (!loginMode) {
                CustomTextField(
                    "email",
                    tfEmail,
                    { tfEmail = it },
                    Modifier.padding(bottom = 10.dp))
            }
            CustomTextField(
                if (loginMode) "username or email" else "username",
                tfUsername,
                { tfUsername = it },
                Modifier.padding(bottom = 10.dp)
            )
            CustomTextField(
                "password",
                tfPassword,
                { tfPassword = it },
                Modifier.padding(bottom = 30.dp)
            )

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.5f.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            ) {
                val buttonMod = Modifier.height(60.dp)
                // toggle sign-in / login
                BlackOrWhiteButton(
                    text = if (loginMode) "Sign-in" else "Login",
                    modifier = buttonMod.weight(2f),
                    active = false,
                    onClick = {
                        loginMode = !loginMode
                    })

                // Send api request
                BlackOrWhiteButton(
                    text = if (loginMode) "Login ->" else "Sign-in ->",
                    modifier = buttonMod.weight(3f),
                    active = true,
                    onClick = {
                        if (loginMode) {
                            val body = FormBody.Builder()
                                .add("username", tfUsername)
                                .add("password", tfPassword)
                                .build()

                            api.post(
                                "https://musicroom.nalebrun.be/auth/login",
                                body,
                                onResponse = { _, response ->
                                    tvResponse = response.body?.string().toString()
                                },
                                onFailure = { _, e ->
                                    println(e)
                                    tvResponse = e.message.toString()
                                }
                            )
                        } else {
                            val body = FormBody.Builder()
                                .add("username", tfUsername)
                                .add("password", tfPassword)
                                .add("email", tfEmail)
                                .build()

                            api.post(
                                "https://musicroom.nalebrun.be/auth/new_account",
                                body,
                                onResponse = { _, response ->
                                    tvResponse = response.body?.string().toString()
                                },
                                onFailure = { _, e ->
                                    println(e)
                                    tvResponse = e.message.toString()
                                }
                            )
                        }
                    })
            }
            Text(tvResponse)

        }
    }

}

// @Preview(showBackground = true)
// @Composable
// fun DefaultPreview() {
//     AuthUi()
// }
