package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class apiSigninJson(
    val message: List<String>,
    val error: String,
    val statusCode: Int
)
