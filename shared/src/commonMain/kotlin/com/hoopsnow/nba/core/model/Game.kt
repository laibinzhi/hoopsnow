package com.hoopsnow.nba.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Int,
    val date: String,
    val season: Int,
    val homeTeamScore: Int,
    val visitorTeamScore: Int,
    val homeTeam: Team,
    val visitorTeam: Team,
    val status: String = if (homeTeamScore > 0 || visitorTeamScore > 0) "Final" else "Scheduled",
)
