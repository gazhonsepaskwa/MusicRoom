package be.nalebrun.musicroom.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import be.nalebrun.musicroom.ui.element.BlackOrWhiteButton
import be.nalebrun.musicroom.ui.element.CustomTextField
import be.nalebrun.musicroom.ui.element.Title
import be.nalebrun.musicroom.viewmodel.AuthViewModel

@Composable
fun ResetPasswordUi() {
    val viewModel = hiltViewModel<AuthViewModel>()
    val email = remember { mutableStateOf("") }
    val context = LocalContext.current

    Column() {
        Title("Reset Password")
        CustomTextField("email", email.value, { email.value = it })
        BlackOrWhiteButton("reset password", active = true) {

            viewModel.forgotPassword(email.value) {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_EMAIL)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Open mail app"))
            }
        }
    }
}