package be.nalebrun.musicroom.ui.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.CustomTextField
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.AuthViewModel
import be.nalebrun.musicroom.viewmodel.NavigationViewModel

@Composable
fun ResetPasswordConfirmationUi(
    email: String?,
    token: String?
) {
    val activity = LocalActivity.current
    val navigationViewModel: NavigationViewModel = if (activity != null) {
        hiltViewModel(activity as ViewModelStoreOwner)
    } else {
        hiltViewModel()
    }
    val authViewModel: AuthViewModel = hiltViewModel()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title("Reset Password")

        Spacer(modifier = Modifier.height(20.dp))

        CustomTextField(
            title = "New Password",
            text = newPassword,
            onValueChange = { newPassword = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomTextField(
            title = "Confirm Password",
            text = confirmPassword,
            onValueChange = { confirmPassword = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(40.dp))

        BlackOrWhiteButton(
            text = "Reset Password",
            active = true,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onClick = {
                if (newPassword.isEmpty()) {
                    navigationViewModel.showMessage("Password cannot be empty")
                } else if (newPassword != confirmPassword) {
                    navigationViewModel.showMessage("Passwords don't match")
                } else if (email == null || token == null) {
                    navigationViewModel.showMessage("Invalid reset link")
                } else {
                    authViewModel.resetPassword(email, token, newPassword) {
                        navigationViewModel.showMessage("Password changed successfully")
                        navigationViewModel.navigateTo("auth") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        )
    }
}
