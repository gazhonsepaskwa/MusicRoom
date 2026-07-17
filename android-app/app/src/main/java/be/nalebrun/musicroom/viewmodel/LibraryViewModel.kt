package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.libraryJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository
) : ViewModel() {
    private val _playlists = MutableStateFlow<List<libraryJson>?>(null)

    val playlists: StateFlow<List<libraryJson>?> = _playlists

    fun getPlaylists() { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
            url = "https://musicroom.nalebrun.be/playlists/available",
            auth = "Bearer $jwt",
            onResponse = { _, response ->
                val res = Json.decodeFromString<List<libraryJson>>(response.body?.string() ?: "")
//                response.body?.string()?.let { Log.d("LIBRARY",it) }
                _playlists.value = res
                Log.d("LIBRARY", "HA S BE EN CALLED")

            },
            onFailure = { _, _ -> }
        )
    }
    }}
}