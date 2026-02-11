package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.database.dao.TeamDao
import com.hoopsnow.nba.core.database.model.asEntity
import com.hoopsnow.nba.core.database.model.asExternalModel
import com.hoopsnow.nba.core.model.Team
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class OfflineFirstTeamsRepository @Inject constructor(
    private val teamDao: TeamDao,
    private val networkDataSource: NbaNetworkDataSource,
) : TeamsRepository {

    override fun getTeams(): Flow<List<Team>> =
        teamDao.getTeams()
            .map { entities -> entities.map { it.asExternalModel() } }
            .onStart { syncTeams() }

    override fun getTeamsByConference(conference: String): Flow<List<Team>> =
        teamDao.getTeamsByConference(conference)
            .map { entities -> entities.map { it.asExternalModel() } }
            .onStart { syncTeams() }

    override fun getTeamById(id: Int): Flow<Team?> =
        teamDao.getTeamById(id)
            .map { it?.asExternalModel() }

    override fun getTeamsByIds(ids: Set<Int>): Flow<List<Team>> =
        teamDao.getTeamsByIds(ids)
            .map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun syncTeams() {
        try {
            val networkTeams = networkDataSource.getTeams()
            val teams = networkTeams.map { it.asExternalModel() }
            teamDao.upsertTeams(teams.map { it.asEntity() })
        } catch (e: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }
}
