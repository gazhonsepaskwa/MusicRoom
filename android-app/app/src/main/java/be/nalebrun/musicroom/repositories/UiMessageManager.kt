package be.nalebrun.musicroom.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager to handle UI messages (like Snakbars) from anywhere in the app.
 * Being a Singleton allows it to be injected into ViewModels.
 */
@Singleton
class UiMessageManager @Inject constructor() {
    private val _messageEvent = MutableLiveData<String?>(null)
    val messageEvent: LiveData<String?> = _messageEvent

    /**
     * Request to display a message to the user
     */
    fun showMessage(message: String) {
        _messageEvent.postValue(message)
    }

    /**
     * Clear the message event after it's been handled
     */
    fun clearMessageEvent() {
        _messageEvent.postValue(null)
    }
}
