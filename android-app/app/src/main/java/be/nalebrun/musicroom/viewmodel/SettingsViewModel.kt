package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import be.nalebrun.musicroom.repositories.UiMessageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    /**
     * Update the server URL in the repository and clear the JWT
     * @param url The new server URL
     * @param onUrlChanged Callback to execute after the URL is updated
     */
    fun updateServerUrl(url: String, onUrlChanged: () -> Unit) {
        viewModelScope.launch {
            val currentUrl = settingsRepository.serverUrlFlow.first()
            if (currentUrl != url) {
                settingsRepository.setServerUrl(url)
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
            // TODO: Implement password change logic
        val body = FormBody.Builder().add("old_password", oldPassword).add("new_password", newPassword).build()
        apiRepository.patch(
            url = "auth/change_password",
            body = body,
            onResponse = { _, response ->
                if (response.code in 200..<300) {
                    uiMessageManager.showMessage("Password changed successfully")
                }
                uiMessageManager.showMessage(response.message)

            },
            onFailure = { _, e ->
                Log.e("SettingsViewModel", "Failed to change password", e)
            }
        )
    }}
}
