package com.hoopsnow.nba.ui.teams

import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

sealed interface TeamsUiState {
    data object Loading : TeamsUiState
    data object Empty : TeamsUiState
    data class Success(val teams: List<Team>) : TeamsUiState
    data class Error(val message: String) : TeamsUiState
}

sealed interface TeamDetailUiState {
    data object Loading : TeamDetailUiState
    data class Success(
        val team: Team,
        val players: List<Player>,
        val recentGames: List<Game>,
    ) : TeamDetailUiState
    data class Error(val message: String) : TeamDetailUiState
}
