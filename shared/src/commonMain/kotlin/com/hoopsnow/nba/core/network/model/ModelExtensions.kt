package com.hoopsnow.nba.core.network.model

import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

/**
 * Convert NetworkTeam to domain Team model
 */
fun NetworkTeam.asExternalModel(): Team = Team(
    id = id,
    conference = conference,
    division = division,
    city = city,
    name = name,
    fullName = fullName,
    abbreviation = abbreviation,
)

/**
 * Convert NetworkPlayer to domain Player model
 */
fun NetworkPlayer.asExternalModel(): Player = Player(
    id = id,
    firstName = firstName,
    lastName = lastName,
    position = position,
    team = team?.asExternalModel(),
)

/**
 * Convert NetworkGame to domain Game model
 */
fun NetworkGame.asExternalModel(): Game = Game(
    id = id,
    date = date,
    season = season,
    homeTeamScore = homeTeamScore,
    visitorTeamScore = visitorTeamScore,
    homeTeam = homeTeam?.asExternalModel() ?: Team(
        id = 0,
        conference = "",
        division = "",
        city = "",
        name = "Unknown",
        fullName = "Unknown Team",
        abbreviation = "UNK",
    ),
    visitorTeam = visitorTeam?.asExternalModel() ?: Team(
        id = 0,
        conference = "",
        division = "",
        city = "",
        name = "Unknown",
        fullName = "Unknown Team",
        abbreviation = "UNK",
    ),
)
