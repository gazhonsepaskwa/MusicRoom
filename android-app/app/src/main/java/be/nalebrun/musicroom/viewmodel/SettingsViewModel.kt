package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.apiChangePasswordFailureJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiServerCheck
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import be.nalebrun.musicroom.repositories.UiMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import javax.inject.Inject

/**
 * The logic for the settings page
 * @author nalebrun
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val credentialRepository: ICredentialRepository,
    private val apiRepository: IAPIRepository,
    private val uiMessageManager: UiMessageManager
) : ViewModel() {

    val serverUrl: StateFlow<String> = settingsRepository.serverUrlFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val debugText: StateFlow<String> = settingsRepository.debugTextFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    /**
     * Update the debug text in the repository
     * @param text The new debug text
     */
    fun updateDebugText(text: String) {
        viewModelScope.launch {
            settingsRepository.setDebugText(text)
        }
    }

    /**
     * Update the server URL in the repository and clear the JWT
     * @param url The new server URL
     * @param onUrlChanged Callback to execute after the URL is updated
     */
    fun updateServerUrl(url: String, onUrlChanged: () -> Unit) {
        viewModelScope.launch {
            val baseUrl = if (url.startsWith("https://")) {url.replace("https://", "")} else {url}
            Log.d("SettingsViewModel", "Updating server URL to $baseUrl")
            val currentUrl = settingsRepository.serverUrlFlow.first()
            if (currentUrl != baseUrl) {
                settingsRepository.setServerUrl(baseUrl)
                credentialRepository.setJWT("") // Clear JWT as it's likely invalid for the new server
                uiMessageManager.showMessage("Server URL updated to $url")
                onUrlChanged()
            }
        }
    }

    /**
     * Change the password of the user
     * @param oldPassword The old password
     * @param newPassword The new password
     * @author nalebrun
     */
    fun changePassword(oldPassword: String, newPassword: String) { viewModelScope.launch {
        // don't send the old password if it's empty and that the password is NULL on the DB
        val body = if (oldPassword.isEmpty()) {
            // TODO check that the account is a google one
            FormBody.Builder().add("newPassword", newPassword).build()
        } else {
            FormBody.Builder().add("oldPassword", oldPassword).add("newPassword", newPassword).build()
        }
        Log.d("SettingsViewModel", "Changing password with oldPassword: $oldPassword, newPassword: $newPassword")
        apiRepository.patch(
            url = "users/change-password",
            auth = "Bearer ${credentialRepository.jwtFlow.first()}",
            body = body,
            onResponse = { _, response ->
                val bodyString = response.body?.string() ?: ""
                if (response.code in 200..<300) {
                    uiMessageManager.showMessage("Password changed successfully")
                } else {
                    Log.e("SettingsViewModel", "Failed to change password: ${Json.decodeFromString<apiChangePasswordFailureJson>(bodyString).message.first()}")
                    uiMessageManager.showMessage(Json.decodeFromString<apiChangePasswordFailureJson>(bodyString).message.first())
                }

            },
            onFailure = { _, e ->
                Log.e("SettingsViewModel", "Failed to change password", e)
                uiMessageManager.showMessage("Network error")
            }
        )
    }}

    fun ifServerUrlValid(url: String, onValid: () -> Unit) { viewModelScope.launch {
        Log.d("SettingsViewModel", "Testing connection to $url")
        val baseUrl = if (url.startsWith("https://")) {url} else {"https://$url"}
        val fullUrl = "$baseUrl/server-check"
        apiRepository.get(
            url = fullUrl,
            auth = "Bearer ${credentialRepository.jwtFlow.first()}",
            onResponse = { _, response ->
                if (response.code in 200..<300) {
                    try {
                        val decodedJson = Json.decodeFromString<apiServerCheck>(response.body?.string() ?: "")
                        if (decodedJson.server_name == "musicroom") {
                            Log.d("SettingsViewModel", decodedJson.server_name)
                        }
                    } catch (e: Exception) {
                        uiMessageManager.showMessage("Invalid server URL")
                        Log.e("SettingsViewModel", "Failed to decode JSON", e)
                    }
                    viewModelScope.launch { onValid() }
                } else {
                    uiMessageManager.showMessage("Invalid server URL")
                }
            },
            onFailure = { _, e ->
                Log.e("SettingsViewModel", "Failed to test connection", e)
                uiMessageManager.showMessage("Invalid server URL")
            }
        )

    }}

    fun deleteAccount(password: String, onSuccess: () -> Unit) { viewModelScope.launch {
        val body = FormBody.Builder().add("password", password).build()

        apiRepository.post(
            "/auth/delete-account",
            body = body,
            auth = "Bearer ${credentialRepository.jwtFlow.first()}",
            onResponse = {_, response ->
                if (response.code in 200..<300) {
                    uiMessageManager.showMessage("Account deleted successfully")
                    viewModelScope.launch {
                        credentialRepository.setJWT("")
                        onSuccess()
                    }
                } else {
                    uiMessageManager.showMessage("Password is incorrect") // (probably)
                    Log.e("SettingsViewModel", "Failed to delete account: ${response.body?.string()}")
                }
            },
            onFailure = { _, e ->
                uiMessageManager.showMessage("Failed to delete your account")
                Log.e("SettingsViewModel", "Failed to delete account", e)
            }
        )

    }}
}


