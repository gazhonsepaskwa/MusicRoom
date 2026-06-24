package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.repositories.CredentialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.FormBody

class AuthViewModelFactory(
    private val APIRepository: APIRepository,
    private val CredetialRepository: CredentialRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(APIRepository, CredetialRepository) as T
    }
}

/**
 * The logic for the Authentification page
 */
class AuthViewModel(
    val apiRepository: APIRepository,
    val credentialRepository: CredentialRepository
) : ViewModel() {

    // login

    private val _loginResult = MutableStateFlow<String?>(null)
    private val _loginOk = MutableStateFlow<Boolean?>(false)
    val loginResult: StateFlow<String?> = _loginResult
    val loginOk: StateFlow<Boolean?> = _loginOk

    fun login(username: String, password: String) { viewModelScope.launch {
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        apiRepository.post(
            url = "https://musicroom.nalebrun.be/auth/login",
            body = body,
            onResponse = { _, response ->
                _loginResult.value = response.body?.string()
                if (response.code in 200..<300) {
                    _loginOk.value = true
                } else {
                    _loginOk.value = false
                }
            },
            onFailure = { _, e ->
                _loginResult.value = e.message
                _loginOk.value = false
            }
        )
    }}

    // signin

    private val _signinResult = MutableStateFlow<String?>(null)
    private val _signinOk = MutableStateFlow<Boolean?>(false)
    val signinResult: StateFlow<String?> = _signinResult
    val signinOk: StateFlow<Boolean?> = _signinOk

    fun signin(username: String, password: String, email: String) { viewModelScope.launch {
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("email", email)
            .build()

        apiRepository.post(
            url = "https://musicroom.nalebrun.be/auth/new_account",
            body = body,
            onResponse = { _, response ->
                _signinResult.value = response.body?.string()
                if (response.code in 200..<300) {
                    _signinOk.value = true;
                } else {
                    _signinOk.value = false;
                }
            },
            onFailure = { _, e ->
                _signinResult.value = e.message
                _signinOk.value = false;
            }
        )
    }}

    // check for skipping
    fun skipIfAlreadyAuthenticate() { viewModelScope.launch {
        credentialRepository.jwtFlow.collect { jwt ->
            apiRepository.get(
                url = "https://musicroom.nalebrun.be/auth/profile",
                auth = jwt,
                onResponse = { _, response ->
                    Log.i("api", "success")
                },
                onFailure = { _, e ->
                    Log.i("api", "failure")
                }
            )
        }
    }}

}
