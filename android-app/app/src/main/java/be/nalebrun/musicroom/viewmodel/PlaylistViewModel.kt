package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Response

class PlaylistViewModelFactory(
    private val APIRepository: APIRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaylistViewModel(APIRepository) as T
    }
}

class PlaylistViewModel(
    val apiRepository: APIRepository
) : ViewModel() {
    private val _test = MutableStateFlow<String?>(null)
    private val _isPublic = MutableStateFlow<Boolean?>(false)
    private val _name = MutableStateFlow<String?>("")
    private val _friends = MutableStateFlow<Int?>(0)
    private val _songsNumber = MutableStateFlow<Int?>(0)
    val test: StateFlow<String?> = _test
    val isPublic: StateFlow<Boolean?> = _isPublic
    val name: StateFlow<String?> = _name
    val friends: StateFlow<Int?> = _friends
    val songsNumber: StateFlow<Int?> = _songsNumber


    fun getPlaylistInfo(id: Int) { viewModelScope.launch {
        apiRepository.get(
            //TODO: change 1 in url to id
            url = "https://musicroom.nalebrun.be/playlists/get/1",
            onResponse = { _, response ->
                if (response.code in 200..<300) {
                    _test.value = "hello"
                }
            },
            onFailure = { _, e -> }
        )
    }
        Log.i("api", "test")

    }

    fun getFriendsInfo(id: Int) { viewModelScope.launch {
        apiRepository.get(
            url = "https://musicroom.nalebrun.be/getsomethingidktheroute",
            onResponse = { _, response -> _test.value = "hello" },
            onFailure = { _, e -> }
        )
    }}
}