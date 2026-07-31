package be.nalebrun.musicroom.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.apiLoginFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiLoginSuccessJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiSigninFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiSigninSuccessJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import be.nalebrun.musicroom.repositories.MusicRepository
import be.nalebrun.musicroom.repositories.UiMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import javax.inject.Inject
import kotlin.code

/**
 * The logic for the Authentification page
 * @author nalebrun
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    val apiRepository:        IAPIRepository,
    val credentialRepository: ICredentialRepository,
    val settingsRepository:   ISettingsRepository,
    val uiMessageManager:     UiMessageManager
) : ViewModel() {

    init {
        skipIfAlreadyAuthenticate()
    }

    // login

    // Result message from the last login attempt
    private val _loginResult = MutableStateFlow<String?>(null)
    // Whether the login was successful
    private val _loginOk = MutableStateFlow<Boolean?>(false)
    val loginResult: StateFlow<String?> = _loginResult
    val loginOk: StateFlow<Boolean?> = _loginOk

    private val _logoutComplete = MutableStateFlow(false)
    val          logoutComplete = _logoutComplete.asStateFlow()

    /**
     * Login a user
     * @param username The user's username
     * @param password The user's password
     * @author nalebrun
     */
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
                    _loginResult.value = Json.decodeFromString<apiLoginFailureJson>(bodyString).message.first()
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


    // Result message from the last signup attempt
    // TODO change everywhere signin by signup
    private val _signinResult = MutableStateFlow<String?>(null)
    val signinResult: StateFlow<String?> = _signinResult

    /**
     * Signup a new user account
     * @param username The desired username
     * @param password The desired password
     * @param email    The desired email address
     * @author nalebrun
     */
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

    /**
     * Google authentication
     */
    fun googleAuth(context: Context) { viewModelScope.launch {
        var baseUrl = apiRepository.getBaseUrl()
        if (baseUrl.isEmpty()) {
            Log.e("AuthViewModel", "Cannot start Google Auth: Base URL is empty")
            return@launch
        }

        if (!baseUrl.startsWith("http")) {
            baseUrl = "https://$baseUrl"
        }

        val url = if (baseUrl.endsWith("/")) "${baseUrl}auth/oauth" else "$baseUrl/auth/oauth"

        // launch browser via an intent
        Log.d("AuthViewModel", "Opening Google Auth URL: $url")
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Failed to open browser for Google Auth", e)
        }
    }}

    /**
     * Check if the user is already logged in and skip the login page in that case
     * @author nalebrun
     */
    fun skipIfAlreadyAuthenticate() { viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    apiRepository.get(
                        url = "auth/profile",
                        auth = "Bearer $jwt",
                        onResponse = { _, response ->
                            if (response.code in 200..<300) {
                                Log.i("AuthViewModel", "user logged in, skipping login page")
                                _loginOk.value = true
                            }
                            else {
                                Log.i("AuthViewModel", "user not already logged in")
                            }
                        },
                        onFailure = { _, e ->
                            Log.i("AuthViewModel", "user not already logged in")
                        }
                    )
                }
            }
        }
    }

    /**
     * Logout the user. First delete the device from the server, delete the jwt, and redirect to the login page
     * @author nalebrun
     */
    fun logout() { viewModelScope.launch {
        val deviceId   = settingsRepository.deviceUuidFlow.firstOrNull() ?: ""
        val deviceName = settingsRepository.deviceNameFlow.firstOrNull() ?: ""
        Log.d("AuthViewModel", "Logging out device $deviceId ($deviceName)")
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            if (jwt.isNotEmpty()) {
                apiRepository.delete(
                    url = "/devices/remove-device",
                    body = FormBody.Builder().add("deviceId", deviceId).add("deviceName", deviceName).build(),
                    auth = "Bearer $jwt",
                    onResponse = { _, response ->
                        Log.d("AuthViewModel", "Logout response: ${response.code}")
                        if (response.code in 200..<300) {
                            viewModelScope.launch {
                                credentialRepository.setJWT("")
                                _loginOk.value = false
                                _logoutComplete.value = true
                            }
                        }
                        else {
                            uiMessageManager.showMessage("Failed to log you out from server")
                        }
                    },
                    onFailure = { _, e ->
                        uiMessageManager.showMessage("Network error")
                    }
                )
            }
        }
    }}
    fun resetLogoutComplete() {
        _logoutComplete.value = false
    }
}
