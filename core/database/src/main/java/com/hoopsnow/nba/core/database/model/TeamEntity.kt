package com.hoopsnow.nba.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hoopsnow.nba.core.model.Team

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: Int,
    val conference: String,
    val division: String,
    val city: String,
    val name: String,
    val fullName: String,
    val abbreviation: String,
)

fun TeamEntity.asExternalModel() = Team(
    id = id,
    conference = conference,
    division = division,
    city = city,
    name = name,
    fullName = fullName,
    abbreviation = abbreviation,
)

fun Team.asEntity() = TeamEntity(
    id = id,
    conference = conference,
    division = division,
    city = city,
    name = name,
    fullName = fullName,
    abbreviation = abbreviation,
)
