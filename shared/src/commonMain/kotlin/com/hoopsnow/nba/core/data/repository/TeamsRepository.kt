package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.model.Team
import kotlinx.coroutines.flow.Flow

/**
 * Repository for teams data.
 */
interface TeamsRepository {
    fun getTeams(): Flow<List<Team>>
    fun getTeamById(id: Int): Flow<Team?>
    suspend fun syncTeams()
}
