package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class apiChangePasswordFailureJson (
    val statusCode: Int,
    val message: List<String>,
    val error: String
)