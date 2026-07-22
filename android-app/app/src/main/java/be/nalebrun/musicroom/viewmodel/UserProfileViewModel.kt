package be.nalebrun.musicroom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.nalebrun.musicroom.IAPIRepository
import be.nalebrun.musicroom.apiJsonStruct.responds.FriendRequestStatus
import be.nalebrun.musicroom.apiJsonStruct.responds.MusicJson
import be.nalebrun.musicroom.apiJsonStruct.responds.UserProfileJson
import be.nalebrun.musicroom.repositories.ICredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val apiRepository: IAPIRepository,
    private val credentialRepository: ICredentialRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfileJson?>(null)
    val profile: StateFlow<UserProfileJson?> = _profile

    private val _favoriteMusics = MutableStateFlow<List<MusicJson>>(emptyList())
    val favoriteMusics: StateFlow<List<MusicJson>> = _favoriteMusics

    private val _friendRequestState = MutableStateFlow<FriendRequestStatus?>(FriendRequestStatus.NOTVIEWED) // understand as not friends
    val friendRequestState : StateFlow<FriendRequestStatus?> = _friendRequestState

    fun sendFriendRequest(userId: Int) {
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                val body = """{"receiverId": $userId}""".toRequestBody("application/json".toMediaType())
                Log.d("API", "Sending friend request to $userId")
                apiRepository.post(
                    "friendship/send-friend-request/",
                    body,
                    "Bearer $jwt",
                    { _, response ->
                        if (response.code in 200..<300) {
                            _friendRequestState.value = FriendRequestStatus.PENDING
                        } else {
                            Log.d("API", "Error sending friend request: ${response.code}")
                            _friendRequestState.value = FriendRequestStatus.PENDING
                        }
                    },
                    { _, e -> e.printStackTrace() }
                )
            }
        }
    }

    fun fetchProfile(userId: Int) {
        viewModelScope.launch {
            credentialRepository.jwtFlow.firstOrNull()?.let { jwt ->
                apiRepository.get(
                    "users/profile/$userId",
                    "Bearer $jwt",
                    { _, response ->
                        if (response.code in 200..<300) {
                            val body = response.body?.string()
                            if (body != null) {
                                try {
                                    val parsedProfile = Json.decodeFromString<UserProfileJson>(body)
                                    _friendRequestState.value = parsedProfile.isFriend
                                    _profile.value = parsedProfile
                                    fetchFavoriteMusics(parsedProfile, jwt)
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
    }

    private fun fetchFavoriteMusics(profile: UserProfileJson, jwt: String) {
        val ids = listOfNotNull(
            profile.firstPreferedMusicId,
            profile.secondPreferedMusicId,
            profile.thirdPreferedMusicId
        )
        
        val musicsMap = mutableMapOf<Int, MusicJson>()
        ids.forEach { id ->
            apiRepository.get(
                "music/$id",
                "Bearer $jwt",
                { _, response ->
                    if (response.code in 200..<300) {
                        val body = response.body?.string()
                        if (body != null) {
                            try {
                                val music = Json.decodeFromString<MusicJson>(body)
                                synchronized(musicsMap) {
                                    musicsMap[id] = music
                                    // Order them according to the original ids list
                                    val orderedMusics = ids.mapNotNull { musicsMap[it] }
                                    _favoriteMusics.value = orderedMusics
                                }
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
}
