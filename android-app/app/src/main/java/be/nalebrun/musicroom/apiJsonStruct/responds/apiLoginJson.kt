package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class apiLoginJson(
    val message: String,
    val error: String,
    val statusCode: Int
)
