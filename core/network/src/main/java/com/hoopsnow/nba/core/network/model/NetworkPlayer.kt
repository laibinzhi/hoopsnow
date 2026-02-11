package com.hoopsnow.nba.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of a Player
 */
@Serializable
data class NetworkPlayer(
    val id: Int,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    val position: String = "",
    val team: NetworkTeam? = null,
)
