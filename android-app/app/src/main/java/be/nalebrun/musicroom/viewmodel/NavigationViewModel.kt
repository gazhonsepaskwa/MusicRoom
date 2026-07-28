package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * The logic for the navigation between pages
 * @author nalebrun
 */
@HiltViewModel
class NavigationViewModel @Inject constructor(
) : ViewModel() {
    // Event to trigger navigation to a new route
    private val _navigationEvent = MutableLiveData<String?>()
    val navigationEvent : LiveData<String?> = _navigationEvent

    // Event to trigger a back navigation
    private val _backEvent = MutableLiveData<Boolean>()
    val backEvent: LiveData<Boolean> = _backEvent

    /**
     * Request navigation to a specific route
     */
    fun navigateTo(route: String) {
        _navigationEvent.value = route
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
}
