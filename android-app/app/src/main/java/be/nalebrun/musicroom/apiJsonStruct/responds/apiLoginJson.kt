package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class apiLoginFailureJson(
    val message: String,
    val error: String,
    val statusCode: Int
)

@Serializable
data class apiLoginSuccessJson(
    val access_token: String
)
