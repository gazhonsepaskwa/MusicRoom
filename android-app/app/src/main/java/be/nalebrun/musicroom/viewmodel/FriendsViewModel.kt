package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.APIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.FriendRequestActionJson
import be.nalebrun.musicroom.apiJsonStruct.responds.NotificationJson
import be.nalebrun.musicroom.apiJsonStruct.responds.apiFriendJson
import be.nalebrun.musicroom.repositories.CredentialRepository
import be.nalebrun.musicroom.repositories.ISettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@Serializable
data class UpdatePermissionsJson(
    val id: String,
    val friendId: Int,
    val canSeek: Boolean,
    val canTogglePlayPause: Boolean,
    val canModifyMusic: Boolean
)

/**
 * The logic for the Friends page
 * @author nalebrun
 */
@HiltViewModel
class FriendsViewModel @Inject constructor(
    val apiRepository: APIRepository,
    val credentialRepository: CredentialRepository,
    val settingsRepository: ISettingsRepository
) : ViewModel() {

    // List of accepted friends
    private val _friends = MutableStateFlow<List< apiFriendJson>>(emptyList())
    val friends : StateFlow<List<apiFriendJson>> = _friends

    // List of pending friend requests (notifications)
    private val _friendRequests = MutableStateFlow<List<NotificationJson>>(emptyList())
    val friendRequests: StateFlow<List<NotificationJson>> = _friendRequests

    init {
        getFriendList()
        getFriendRequests()
    }

    /**
     * Answers a friend request "ACCEPTED"
     */
    fun acceptFriendRequest(senderId: Int) {
        answerFriendRequest(senderId, "ACCEPTED")
    }

    /**
     * Answers a friend request "REJECTED"
     */
    fun declineFriendRequest(senderId: Int) {
        answerFriendRequest(senderId, "REJECTED")
    }

    private fun answerFriendRequest(senderId: Int, answer: String) {
        Log.d("FriendsViewModel", "Answering friend request: senderId=$senderId, answer=$answer")
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    val action = FriendRequestActionJson(senderId, answer)
                    val bodyString = Json.encodeToString(action)
                    Log.d("FriendsViewModel", "Body: $bodyString")
                    val body = bodyString.toRequestBody("application/json".toMediaType())
                    apiRepository.post(
                        "friendship/answer-friend-request",
                        body,
                        "Bearer $jwt",
                        { _, response ->
                            Log.d("FriendsViewModel", "Response code: ${response.code}")
                            if (response.code in 200..<300) {
                                getFriendRequests()
                                getFriendList()
                            } else {
                                Log.e("FriendsViewModel", "Error answering friend request: ${response.body?.string()}")
                            }
                        },
                        { _, e ->
                            Log.e("FriendsViewModel", "Failure answering friend request", e)
                        }
                    )
                } else {
                    Log.e("FriendsViewModel", "JWT is empty")
                }
            } ?: Log.e("FriendsViewModel", "JWT is null")
        }
    }

    /**
     * Fetch the list of pending friend requests from notifications
     */
    fun getFriendRequests() {
        Log.d("FriendsViewModel", "Fetching friend requests")
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    apiRepository.get(
                        "notifications/pending-notifications",
                        "Bearer $jwt",
                        { _, response ->
                            Log.d("FriendsViewModel", "Get notifications code: ${response.code}")
                            if (response.code in 200..<300) {
                                val body = response.body?.string()
                                Log.d("FriendsViewModel", "Notifications body: $body")
                                if (body != null) {
                                    try {
                                        val notifications = Json.decodeFromString<List<NotificationJson>>(body)
                                        _friendRequests.value = notifications.filter { it.type == "FRIEND_REQUEST" }
                                        Log.d("FriendsViewModel", "Filtered requests: ${_friendRequests.value.size}")
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

    /**
     * Fetch the list of accepted friends
     */
    fun getFriendList() { viewModelScope.launch {
        credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
            if (jwt.isNotEmpty()) {
                apiRepository.get(
                    "friendship/friend-list",
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

    /**
     * Update permissions for a specific friend on the current device
     */
    fun updatePermissions(friendId: Int, canSeek: Boolean, canTogglePlayPause: Boolean, canModifyMusic: Boolean) {
        viewModelScope.launch {
            val deviceId = settingsRepository.deviceUuidFlow.first() ?: return@launch
            val jwt = credentialRepository.jwtFlow.firstOrNull() ?: return@launch
            if (jwt.isEmpty()) return@launch

            val updateJson = UpdatePermissionsJson(
                id = deviceId,
                friendId = friendId,
                canSeek = canSeek,
                canTogglePlayPause = canTogglePlayPause,
                canModifyMusic = canModifyMusic
            )
            val bodyString = Json.encodeToString(updateJson)
            val body = bodyString.toRequestBody("application/json".toMediaType())

            apiRepository.patch(
                "devices/update_permissions",
                body,
                "Bearer $jwt",
                { _, response ->
                    Log.d("FriendsViewModel", "Update permissions response: ${response.code}")
                },
                { _, e ->
                    Log.e("FriendsViewModel", "Update permissions failed", e)
                }
            )
        }
    }

    /**
     * Remove a friend from the friend list
     */
    fun removeFriend(friendId: Int?) {
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                if (jwt.isNotEmpty()) {
                    val bodyString = Json.encodeToString(mapOf("receiverId" to friendId))
                    val body = bodyString.toRequestBody("application/json".toMediaType())
                    apiRepository.delete(
                        "friendship/delete",
                        body,
                        "Bearer $jwt",
                        { _, response ->
                            Log.d("FriendsViewModel", "Remove friend response: ${response.code}")
                            if (response.code in 200..<300) {
                                getFriendList()
                            }
                        },
                        { _, e ->
                            Log.e("FriendsViewModel", "Remove friend failed", e)
                        }
                    )
                }
            }
        }
    }
}
