package com.hoopsnow.nba.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of a Team
 */
@Serializable
data class NetworkTeam(
    val id: Int,
    val abbreviation: String = "",
    val city: String = "",
    val conference: String = "",
    val division: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val name: String = "",
)
