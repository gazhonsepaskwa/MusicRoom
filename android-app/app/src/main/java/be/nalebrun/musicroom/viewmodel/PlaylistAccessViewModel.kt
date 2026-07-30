package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.NotificationJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistAccessJson
import be.nalebrun.musicroom.apiJsonStruct.responds.PlaylistNotificationJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiFriendJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import be.nalebrun.musicroom.repositories.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
//
@HiltViewModel
class PlaylistAccessViewModel @Inject constructor(
    val apiRepository: IAPIRepository,
    val credentialRepository: ICredentialRepository,
    val musicRepository: MusicRepository
) : ViewModel() {
    private val _friends = MutableStateFlow<List<apiFriendJson>>(emptyList())
    private val _friendsWithAccess = MutableStateFlow<List<PlaylistAccessJson>>(emptyList())
    private val _playlistRequests = MutableStateFlow<List<PlaylistNotificationJson>>(emptyList())

    val friends : StateFlow<List<apiFriendJson>> = _friends
    val friendsWithAccess : StateFlow<List<PlaylistAccessJson>> = _friendsWithAccess
    val playlistRequest : StateFlow<List<PlaylistNotificationJson>> = _playlistRequests

    fun getFriends() { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            if (jwt.isNotEmpty()) {
                apiRepository.get(
                    "friendship/friend-list",
                    "Bearer $jwt",
                    { _, response ->
                        if (response.code in 200..<300) {
                            val body = response.body?.string()
                            if (body != null) {
                                try {
                                    val parsedResults = Json.decodeFromString<List<apiFriendJson>>(body)
                                    _friends.value = parsedResults
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    },
                    { _, e -> e.printStackTrace() }
                )
            }

        }
    } }

    fun getFriendsWithAccess(playlistId: Int) { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.get(
                url = "playlistship/allowed-playlist-users/$playlistId",
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        val body = response.body?.string()
                        if (body != null) {
                            try {
                                val parsedResults = Json.decodeFromString<List<PlaylistAccessJson>>(body)
                                _friendsWithAccess.value = parsedResults
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun giveFriendAccessToPlaylist(friendId: Int, playlistId: Int) { viewModelScope.launch {
        val body = """{"playlistId": $playlistId, "addresseeId": $friendId}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.post(
                url = "/playlistship/send-playlist-invitation",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        // ?
                    }
                },
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun answerAccessInvitationToPlaylist(playlistId: Int, status: String) { viewModelScope.launch {
        val body = """{"playlistId": $playlistId, "status": "$status"}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.post(
                url = "/playlistship/answer-playlist-invitation",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response -> },
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun leavePlaylistAccess(playlistId: Int, userId: Int) { viewModelScope.launch {
        val body = """{"playlistId": $playlistId, "addresseeId": $userId}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.post(
                url = "/playlistship/answer-playlist-invitation",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response -> },
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun getFriendRequests() {
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    apiRepository.get(
                        "notifications/pending-notifications",
                        "Bearer $jwt",
                        { _, response ->
                            if (response.code in 200..<300) {
                                val body = response.body?.string()
                                if (body != null) {
                                    try {
                                        val notifications = Json.decodeFromString<List<PlaylistNotificationJson>>(body)
                                        _playlistRequests.value = notifications.filter { it.type == "PLAYLIST_INVITATION" }
                                        Log.d("FriendsViewModel", "Filtered requests: ${_playlistRequests.value.size}")
                                    } catch (e: Exception) {
                                        Log.e("FriendsViewModel", "Parsing error", e)
                                    }
                                }
                            }
                        },
                        { _, e -> Log.e("FriendsViewModel", "Failure getting notifications", e) }
                    )
                }
            }
        }
    }

}
