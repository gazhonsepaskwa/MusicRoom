package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import be.nalebrun.musicroom.APIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.FormBody

class AuthViewModelFactory(
    private val APIRepository: APIRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(APIRepository) as T
    }
}

/**
 * The logic for the Authentification page
 */
class AuthViewModel(
    val APIRepository: APIRepository
) : ViewModel() {


    private val _loginResult = MutableStateFlow<String?>(null)
    private val _loginOk = MutableStateFlow<Boolean?>(false)
    val loginResult: StateFlow<String?> = _loginResult
    val loginOk: StateFlow<Boolean?> = _loginOk

    fun login(username: String, password: String) {
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        APIRepository.post(
            "https://musicroom.nalebrun.be/auth/login",
            body,
            onResponse = { _, response ->
                _loginResult.value = response.body?.string()
                _loginOk.value = true
            },
            onFailure = { _, e ->
                _loginResult.value = e.message
                _loginOk.value = false
            }
        )
    }


    private val _signinResult = MutableStateFlow<String?>(null)
    private val _signinOk = MutableStateFlow<Boolean?>(false)
    val signinResult: StateFlow<String?> = _signinResult
    val signinOk: StateFlow<Boolean?> = _signinOk

    fun signin(username: String, password: String, email: String) {
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("email", email)
            .build()

        APIRepository.post(
            "https://musicroom.nalebrun.be/auth/new_account",
            body,
            onResponse = { _, response ->
                _signinResult.value = response.body?.string()
            },
            onFailure = { _, e ->
                _signinResult.value = e.message
            }
        )
    }
}
