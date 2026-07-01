package be.nalebrun.musicroom.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.viewmodel.AuthViewModel
import be.nalebrun.musicroom.viewmodel.AuthViewModelFactory
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.CustomTextField
import kotlin.math.log

@Composable
fun AuthUi(
    navController: NavController,
    apiRepository: APIRepository,
    credentialRepository: CredentialRepository
) {
    // TODO maybe change the factory by an injection methode (Hilt look to be the right way)
    val viewModel = viewModel<AuthViewModel>(
        factory = AuthViewModelFactory(apiRepository, credentialRepository)
    )
    // get the viewModel var in the AuthUi to update ui when value change
    val loginResult:  String?  by viewModel.loginResult.collectAsStateWithLifecycle()
    val signinResult: String?  by viewModel.signinResult.collectAsStateWithLifecycle()
    val loginOk:      Boolean? by viewModel.loginOk.collectAsStateWithLifecycle()

    // navigation triggers
    LaunchedEffect(loginOk) {
        if (loginOk == true) {
            navController.navigate("playlist")
        }
    }

    //  UI state
    var loginMode  by remember { mutableStateOf(true) }
    var tfUsername by remember { mutableStateOf("") }
    var tfEmail    by remember { mutableStateOf("") }
    var tfPassword by remember { mutableStateOf("") }

    //  Which result to display
    var currentResult: String? = if (loginMode) loginResult else signinResult

    viewModel.skipIfAlreadyAuthenticate()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titles
        Text("Music Room", fontSize = 20.sp)
        Text(
            text = if (loginMode) "Login" else "Sign-in",
            modifier = Modifier.padding(bottom = 30.dp),
            fontSize = 30.sp
        )

        // text fields
        if (!loginMode) {
            CustomTextField(
                "email",
                tfEmail,
                { tfEmail = it },
                Modifier.padding(bottom = 10.dp)
            )
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
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            val buttonMod = Modifier.height(60.dp)
            // toggle sign-in / login
            BlackOrWhiteButton(
                text = if (loginMode) "Sign-in" else "Login",
                modifier = buttonMod.weight(2f),
                active = false,
                onClick = { loginMode = !loginMode }
            )

            // Send api request
            BlackOrWhiteButton(
                text = if (loginMode) "Login ->" else "Sign-in ->",
                modifier = buttonMod.weight(3f),
                active = true,
                onClick = {
                    currentResult = ""
                    if (loginMode) {
                        viewModel.login(tfUsername, tfPassword)
                    } else {
                        viewModel.signin(tfUsername, tfPassword, tfEmail)
                    }
                }
            )
        }
        // Display response
        Text(currentResult ?: "", textAlign = TextAlign.Center)
    }
}
