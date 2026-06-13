package be.nalebrun.musicroom

import android.text.method.TextKeyListener
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException

@Composable
fun CustomTextField(
    title: String,
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
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
            CustomTextField("email", tfEmail, {tfEmail = it} ,Modifier.padding(bottom = 10.dp))
        }
        CustomTextField(if (loginMode) "username or email" else "username", tfUsername, {tfUsername = it},Modifier.padding(bottom = 10.dp))
        CustomTextField("password", tfPassword, {tfPassword= it},Modifier.padding(bottom = 30.dp))

        // Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.5f.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 5.dp)
        ) {
            val buttonMod = Modifier.height(60.dp)
            // toggle sign-in / login
            Black_White_Button(text = if (loginMode) "Sign-in" else "Login",  modifier = buttonMod.weight(2f), active = false, onClick = {
                loginMode = !loginMode
            })

            // Send api request
            Black_White_Button(text = if (loginMode) "Login ->" else "Sign-in ->" , modifier = buttonMod.weight(3f), active = true, onClick = {
                val api = Api()
                if (loginMode) {
                    val body = FormBody.Builder()
                        .add("username", tfUsername)
                        .add("password", tfPassword)
                        .build()

                    api.post("https://musicroom.nalebrun.be/auth/login",
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
                else {
                    val body = FormBody.Builder()
                        .add("username",tfUsername)
                        .add("password",tfPassword)
                        .add("email",   tfEmail)
                        .build()

                    api.post("https://musicroom.nalebrun.be/auth/new_account",
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

/**
 * Class to interact with API
 * @author nalebrun
 * @property client (private) hold the connection client
 */
class Api() {
    // Property
    private val client = OkHttpClient()

    // Methods
    /**
     * Make a GET query to a given url
     * @author nalebrun
     * @param url the url where the request goes
     * @param onResponse callback function that execute when the api respond to the request
     * @param onFailure callback function that execute whet there is an error communicating with api
     */
    fun get(
        url: String,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
            }
        })
    }

    /**
     * Make a POST query to a given url
     * @author nalebrun
     * @param url the url where the request goes
     * @param body the posted body of the request
     * @param onResponse callback function that execute when the api respond to the request
     * @param onFailure callback function that execute whet there is an error communicating with api
     */
    fun post(
        url: String,
        body: RequestBody,
        onResponse: (call: Call, response: Response) -> Unit,
        onFailure: (call: Call, e: IOException) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call,response)
            }
        })
    }
}

