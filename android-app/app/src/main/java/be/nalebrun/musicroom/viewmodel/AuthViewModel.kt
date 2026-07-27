package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.FriendRequestStatus
import be.nalebrun.musicroom.apiJsonStruct.responds.apiLoginFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiLoginSuccessJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiSigninFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiSigninSuccessJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val credentialRepository: ICredentialRepository,
    val settingsRepository: ISettingsRepository
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
        val deviceID = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
        val deviceName = settingsRepository.deviceNameFlow.firstOrNull() ?: ""

        val body = FormBody.Builder()
            .add("username", username)
            .add("deviceID", deviceID)
            .add("deviceName", deviceName)
            .add("password", password)
            .build()

        apiRepository.post(
            url = "auth/login",
            body = body,
            onResponse = { _, response ->
                val bodyString = response.body?.string() ?: ""
                if (response.code in 200..<300) {
                    val jwt = Json.decodeFromString<apiLoginSuccessJson>(bodyString).access_token
                    viewModelScope.launch {
                        //  store the jwt
                        credentialRepository.setJWT(jwt)
                        _loginOk.value = true
                    }
                } else {
                    val failureMessage = Json.decodeFromString<apiLoginFailureJson>(bodyString).message
                    _loginResult.value = failureMessage
                    Log.d("test123", "$failureMessage : ${response.code}")

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
        val deviceID = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
        val deviceName = settingsRepository.deviceNameFlow.firstOrNull() ?: ""

        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("email", email)
            .add("deviceID", deviceID)
            .add("deviceName", deviceName)
            .build()

        apiRepository.post(
            url = "auth/new_account",
            body = body,
            onResponse = { _, response ->
                val bodyString = response.body?.string() ?: ""
                if (response.code in 200..<300) {
                    _signinResult.value = Json.decodeFromString<apiSigninSuccessJson>(bodyString).message
                } else {
                    _signinResult.value = Json.decodeFromString<apiSigninFailureJson>(bodyString).message.first()
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
                        url = "auth/profile",
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
