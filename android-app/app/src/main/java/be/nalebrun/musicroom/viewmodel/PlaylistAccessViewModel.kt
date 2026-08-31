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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
//    private val _friendsWithoutAccess = MutableStateFlow<List<apiFriendJson>>(emptyList())
    private val _playlistRequests = MutableStateFlow<List<PlaylistNotificationJson>>(emptyList())

    val friends : StateFlow<List<apiFriendJson>> = _friends
    val friendsWithAccess : StateFlow<List<PlaylistAccessJson>> = _friendsWithAccess
//    val friendsWithoutAccess : StateFlow<List<apiFriendJson>> = _friendsWithoutAccess
    val playlistRequest : StateFlow<List<PlaylistNotificationJson>> = _playlistRequests

    fun getAccessFriends(playlistId: Int) {
        getFriends()
        getFriendsWithAccess(playlistId)

//        val friendsWithAccessId = _friendsWithAccess.value.map { it.addresseeId }
//        _friendsWithoutAccess.value = _friends.value.filter { it.otherId !in friendsWithAccessId }
    }

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
                        Log.d("Invite to playlist", "Success")
                    } else {
                        Log.d("Invite to playlist", response.body?.string() ?: "")
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
                onResponse = { _, response ->
                    if (response.code in 200..<300) {

                    }
                },
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun leavePlaylistAccess(playlistId: Int, userId: Int = -1) { viewModelScope.launch {
        var newId = userId
        if (userId == -1) {
            val userIdString =  credentialRepository.userId.first()
            newId = userIdString.toInt()
        }

        val body = """{"playlistId": $playlistId, "addresseeId": $newId}""".toRequestBody("application/json".toMediaType())
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            apiRepository.post(
                url = "/playlistship/leave-playlist",
                body = body,
                auth = "Bearer $jwt",
                onResponse = { _, response ->
                    if (response.code in 200..<300) {
                        _friendsWithAccess.value = _friendsWithAccess.value.filter { it.addresseeId != userId }

                    } else {
                        Log.d("Leave playlistship", response.body?.string() ?: "")
                    }
                },
                onFailure = { _, e -> e.printStackTrace() }
            )
        }
    }}

    fun getPlaylistInvitations() {
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
                                        Log.d("PlaylistAccessViewModel", "Requests: ${notifications}\nFiltered requests: ${_playlistRequests.value}")
                                    } catch (e: Exception) {
                                        Log.e("PlaylistAccessViewModel", "Parsing error", e)
                                    }
                                }
                            }
                        },
                        { _, e -> Log.e("PlaylistAccessViewModel", "Failure getting notifications", e) }
                    )
                }
            }
        }
    }

}
