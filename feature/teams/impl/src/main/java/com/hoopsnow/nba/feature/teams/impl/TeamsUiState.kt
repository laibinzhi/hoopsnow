package com.hoopsnow.nba.feature.teams.impl

import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

/**
 * UI state for the Teams list screen
 */
sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data object Empty : TeamsUiState
    data class Success(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

/**
 * UI state for the Team detail screen
 */
sealed interface TeamDetailUiState {
    data object Loading : TeamDetailUiState
    data class Success(
        val team: Team,
        val players: List<Player>,
        val recentGames: List<Game>,
    ) : TeamDetailUiState
    data class Error(val message: String) : TeamDetailUiState
}
