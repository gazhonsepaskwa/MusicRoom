package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class apiSigninFailureJson(
    val message: List<String>,
    val error: String,
    val statusCode: Int
)

@Serializable
data class apiSigninSuccessJson(
    val message: String
)
