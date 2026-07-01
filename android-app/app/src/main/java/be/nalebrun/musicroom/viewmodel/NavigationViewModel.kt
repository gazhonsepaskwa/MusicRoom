package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
) : ViewModel() {
    // create a live data that can be updated but private so only local code can update
    private val _navigationEvent = MutableLiveData<String?>()
    // publish a var that have the update of the data but readonly
    val navigationEvent : LiveData<String?> = _navigationEvent

    fun navigateTo(route: String) {
        _navigationEvent.value = route
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }
}