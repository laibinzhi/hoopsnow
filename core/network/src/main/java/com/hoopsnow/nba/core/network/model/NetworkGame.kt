package com.hoopsnow.nba.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of a Game
 */
@Serializable
data class NetworkGame(
    val id: Int,
    val date: String = "",
    @SerialName("home_team_score")
    val homeTeamScore: Int = 0,
    @SerialName("visitor_team_score")
    val visitorTeamScore: Int = 0,
    @SerialName("home_team")
    val homeTeam: NetworkTeam? = null,
    @SerialName("visitor_team")
    val visitorTeam: NetworkTeam? = null,
    val season: Int = 0,
)
