package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class apiLoginFailureJson(
    val message: String = "",
    val error: String = "",
    val statusCode: Int
)

@Serializable
@JsonIgnoreUnknownKeys
data class apiLoginSuccessJson(
    val access_token: String = ""
)
