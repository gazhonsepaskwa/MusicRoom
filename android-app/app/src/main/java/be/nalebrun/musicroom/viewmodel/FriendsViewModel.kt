package be.nalebrun.musicroom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.SearchResponseJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiFriendJson
import be.nalebrun.musicroom.repositories.CredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    val apiRepository: APIRepository,
    val credentialRepository: CredentialRepository
) : ViewModel() {

    private val _friends = MutableStateFlow<List< apiFriendJson>>(emptyList())
    val friends : StateFlow<List<apiFriendJson>> = _friends

    init {
        getFriendList()
    }

    fun getFriendList() { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            if (jwt.isNotEmpty()) {
                apiRepository.get(
                    "https://musicroom.nalebrun.be/friendship/friend-list",
                    "Bearer $jwt",
                    { _, response ->
                        if (response.code in 200 ..<300) {
                            val body = response.body?.string()
                            if (body != null) {
                                try {
                                    val parsedResults = Json.decodeFromString<List< apiFriendJson>>(body)
                                    _friends.value = parsedResults
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },
                    { _, e ->
                        e.printStackTrace()
                    }
                )
            }
        }
    }}

}