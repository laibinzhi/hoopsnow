package com.hoopsnow.nba.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Team

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val date: String,
    val season: Int,
    val homeTeamScore: Int,
    val visitorTeamScore: Int,
    val status: String,
    // Home team
    val homeTeamId: Int,
    val homeTeamConference: String,
    val homeTeamDivision: String,
    val homeTeamCity: String,
    val homeTeamName: String,
    val homeTeamFullName: String,
    val homeTeamAbbreviation: String,
    // Visitor team
    val visitorTeamId: Int,
    val visitorTeamConference: String,
    val visitorTeamDivision: String,
    val visitorTeamCity: String,
    val visitorTeamName: String,
    val visitorTeamFullName: String,
    val visitorTeamAbbreviation: String,
)

fun GameEntity.asExternalModel() = Game(
    id = id,
    date = date,
    season = season,
    homeTeamScore = homeTeamScore,
    visitorTeamScore = visitorTeamScore,
    status = status,
    homeTeam = Team(
        id = homeTeamId,
        conference = homeTeamConference,
        division = homeTeamDivision,
        city = homeTeamCity,
        name = homeTeamName,
        fullName = homeTeamFullName,
        abbreviation = homeTeamAbbreviation,
    ),
    visitorTeam = Team(
        id = visitorTeamId,
        conference = visitorTeamConference,
        division = visitorTeamDivision,
        city = visitorTeamCity,
        name = visitorTeamName,
        fullName = visitorTeamFullName,
        abbreviation = visitorTeamAbbreviation,
    ),
)

fun Game.asEntity() = GameEntity(
    id = id,
    date = date,
    season = season,
    homeTeamScore = homeTeamScore,
    visitorTeamScore = visitorTeamScore,
    status = status,
    homeTeamId = homeTeam.id,
    homeTeamConference = homeTeam.conference,
    homeTeamDivision = homeTeam.division,
    homeTeamCity = homeTeam.city,
    homeTeamName = homeTeam.name,
    homeTeamFullName = homeTeam.fullName,
    homeTeamAbbreviation = homeTeam.abbreviation,
    visitorTeamId = visitorTeam.id,
    visitorTeamConference = visitorTeam.conference,
    visitorTeamDivision = visitorTeam.division,
    visitorTeamCity = visitorTeam.city,
    visitorTeamName = visitorTeam.name,
    visitorTeamFullName = visitorTeam.fullName,
    visitorTeamAbbreviation = visitorTeam.abbreviation,
)
