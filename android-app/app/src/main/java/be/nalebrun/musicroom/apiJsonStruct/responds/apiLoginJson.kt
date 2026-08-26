package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class apiLoginFailureJson(
    val message: List<String> = emptyList(),
    val error: String = "",
    val statusCode: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class apiLoginSuccessJson(
    val access_token: String = ""
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class apiUserProfileJson(
    val id: Int,
    val username: String
)