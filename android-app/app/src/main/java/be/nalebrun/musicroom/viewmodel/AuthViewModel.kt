package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import androidx.navigation.NavController
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.apiLoginFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiLoginSuccessJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiSigninFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiSigninSuccessJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import be.nalebrun.musicroom.repositories.CredentialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import javax.inject.Inject
import kotlin.code

/**
 * The logic for the Authentification page
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {

    init {
        skipIfAlreadyAuthenticate()  // Runs ONCE when ViewModel is created
    }

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
                if (response.code in 200..<300) {
                    val jwt = Json.decodeFromString<apiLoginSuccessJson>(response.body?.string() ?: "").access_token
                    viewModelScope.launch {
                        //  store the jwt
                        credentialRepository.setJWT(jwt)
                        _loginOk.value = true
                    }
                } else {
                    _loginResult.value = Json.decodeFromString< apiLoginFailureJson>(response.body?.string() ?: "").message
                    _loginOk.value = false
                }
                response.close()
            },
            onFailure = { _, e ->
                _loginResult.value = e.message
                _loginOk.value = false
            }
        )
    }}

    // signin

    private val _signinResult = MutableStateFlow<String?>(null)
    val signinResult: StateFlow<String?> = _signinResult

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
                if (response.code in 200..<300) {
                    _signinResult.value = Json.decodeFromString<apiSigninSuccessJson>(response.body?.string() ?: "").message
                } else {
                    _signinResult.value = Json.decodeFromString<apiSigninFailureJson>(response.body?.string() ?: "").message.first()
                }
                response.close()
            },
            onFailure = { _, e ->
                _signinResult.value = e.message
            }
        )
    }}

    // check for skipping
    fun skipIfAlreadyAuthenticate() { viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    apiRepository.get(
                        url = "https://musicroom.nalebrun.be/auth/profile",
                        auth = "Bearer $jwt",
                        onResponse = { _, response ->
                            if (response.code in 200..<300) {
                                Log.i("api", "user logged in, skipping login page")
                                _loginOk.value = true
                            }
                            else {
                                Log.i("api", "user not already logged in")
                            }
                        },
                        onFailure = { _, e ->
                            Log.i("api", "user not already logged in")
                        }
                    )
                }
            }
        }
    }


}
