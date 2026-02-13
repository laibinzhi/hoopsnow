package com.hoopsnow.nba.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hoopsnow.nba.core.database.NbaDatabase
import com.hoopsnow.nba.core.database.TeamEntity
import com.hoopsnow.nba.core.model.Team
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.asExternalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class OfflineFirstTeamsRepository(
    private val database: NbaDatabase,
    private val networkDataSource: NbaNetworkDataSource,
) : TeamsRepository {

    private val teamQueries get() = database.teamQueries

    override fun getTeams(): Flow<List<Team>> =
        teamQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toTeam() } }
            .onStart { syncTeams() }

    override fun getTeamById(id: Int): Flow<Team?> =
        teamQueries.getById(id.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.firstOrNull()?.toTeam() }

    override suspend fun syncTeams() {
        try {
            val networkTeams = networkDataSource.getTeams()
            val teams = networkTeams.map { it.asExternalModel() }
            database.transaction {
                teams.forEach { team ->
                    teamQueries.upsert(
                        id = team.id.toLong(),
                        conference = team.conference,
                        division = team.division,
                        city = team.city,
                        name = team.name,
                        fullName = team.fullName,
                        abbreviation = team.abbreviation,
                    )
                }
            }
        } catch (_: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }

    private fun TeamEntity.toTeam(): Team = Team(
        id = id.toInt(),
        conference = conference,
        division = division,
        city = city,
        name = name,
        fullName = fullName,
        abbreviation = abbreviation,
    )
}
