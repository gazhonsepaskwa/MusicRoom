package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The logic for the settings page
 * @author nalebrun
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: ISettingsRepository,
    private val credentialRepository: ICredentialRepository
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
                onUrlChanged()
            }
        }
    }
}
