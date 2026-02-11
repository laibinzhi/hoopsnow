package com.hoopsnow.nba.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val position: String,
    val teamId: Int?,
    val teamConference: String?,
    val teamDivision: String?,
    val teamCity: String?,
    val teamName: String?,
    val teamFullName: String?,
    val teamAbbreviation: String?,
)

fun PlayerEntity.asExternalModel() = Player(
    id = id,
    firstName = firstName,
    lastName = lastName,
    position = position,
    team = if (teamId != null) {
        Team(
            id = teamId,
            conference = teamConference ?: "",
            division = teamDivision ?: "",
            city = teamCity ?: "",
            name = teamName ?: "",
            fullName = teamFullName ?: "",
            abbreviation = teamAbbreviation ?: "",
        )
    } else null,
)

fun Player.asEntity() = PlayerEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    position = position,
    teamId = team?.id,
    teamConference = team?.conference,
    teamDivision = team?.division,
    teamCity = team?.city,
    teamName = team?.name,
    teamFullName = team?.fullName,
    teamAbbreviation = team?.abbreviation,
)
