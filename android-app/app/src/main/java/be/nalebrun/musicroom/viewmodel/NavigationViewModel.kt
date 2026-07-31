package be.nalebrun.musicroom.viewmodel

import be.nalebrun.musicroom.repositories.UiMessageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavOptionsBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Data class to represent a navigation event with optional options
 */
data class NavEvent(
    val route: String,
    val builder: (NavOptionsBuilder.() -> Unit)? = null
)

/**
 * The logic for the navigation between pages
 * @author nalebrun
 */
@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val uiMessageManager: UiMessageManager
) : ViewModel() {
    // Event to trigger navigation to a new route
    private val _navigationEvent = MutableLiveData<NavEvent?>()
    val navigationEvent : LiveData<NavEvent?> = _navigationEvent

    // Event to trigger a back navigation
    private val _backEvent = MutableLiveData<Boolean>()
    val backEvent: LiveData<Boolean> = _backEvent

    // Event to trigger a message (Snackbar)
    val messageEvent: LiveData<String?> = uiMessageManager.messageEvent

    /**
     * Request navigation to a specific route
     */
    fun navigateTo(route: String, builder: (NavOptionsBuilder.() -> Unit)? = null) {
        _navigationEvent.value = NavEvent(route, builder)
    }

    /**
     * Clear the current navigation event after it's been handled
     */
    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }

    /**
     * Request to go back to the previous screen
     */
    fun navigateBack() {
        _backEvent.value = true
    }

    /**
     * Clear the back navigation event after it's been handled
     */
    fun clearBackEvent() {
        _backEvent.value = false
    }

    /**
     * Request to display a message to the user
     */
    fun showMessage(message: String) {
        uiMessageManager.showMessage(message)
    }

    /**
     * Clear the message event after it's been handled
     */
    fun clearMessageEvent() {
        uiMessageManager.clearMessageEvent()
    }
}
