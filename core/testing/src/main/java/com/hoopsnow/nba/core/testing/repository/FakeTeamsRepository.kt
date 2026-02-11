package com.hoopsnow.nba.core.testing.repository

import com.hoopsnow.nba.core.data.repository.TeamsRepository
import com.hoopsnow.nba.core.model.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of [TeamsRepository] for testing.
 */
class FakeTeamsRepository : TeamsRepository {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())

    override fun getTeams(): Flow<List<Team>> = _teams

    override fun getTeamsByConference(conference: String): Flow<List<Team>> = _teams.map { teams ->
        teams.filter { it.conference == conference }
    }

    override fun getTeamById(id: Int): Flow<Team?> = _teams.map { teams ->
        teams.find { it.id == id }
    }

    override fun getTeamsByIds(ids: Set<Int>): Flow<List<Team>> = _teams.map { teams ->
        teams.filter { it.id in ids }
    }

    override suspend fun syncTeams() {
        // No-op for fake
    }

    /**
     * Set teams for testing.
     */
    fun setTeams(teams: List<Team>) {
        _teams.value = teams
    }

    /**
     * Add a team for testing.
     */
    fun addTeam(team: Team) {
        _teams.value = _teams.value + team
    }
}
