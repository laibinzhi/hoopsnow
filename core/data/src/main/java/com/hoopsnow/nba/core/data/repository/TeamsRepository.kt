package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.model.Team
import kotlinx.coroutines.flow.Flow

/**
 * Repository for teams data.
 */
interface TeamsRepository {
    /**
     * Get all teams as a Flow
     */
    fun getTeams(): Flow<List<Team>>

    /**
     * Get teams filtered by conference
     */
    fun getTeamsByConference(conference: String): Flow<List<Team>>

    /**
     * Get a specific team by ID
     */
    fun getTeamById(id: Int): Flow<Team?>

    /**
     * Get teams by IDs
     */
    fun getTeamsByIds(ids: Set<Int>): Flow<List<Team>>

    /**
     * Sync teams from network
     */
    suspend fun syncTeams()
}
