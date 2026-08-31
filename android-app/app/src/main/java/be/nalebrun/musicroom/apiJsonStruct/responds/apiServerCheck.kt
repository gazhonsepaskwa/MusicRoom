package be.nalebrun.musicroom.apiJsonStruct.responds

import kotlinx.serialization.Serializable

@Serializable
data class apiServerCheck (
    val server_name: String = ""
)
