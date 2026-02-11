package com.hoopsnow.nba.feature.games.impl

import com.hoopsnow.nba.core.model.Game

/**
 * UI state for the Games list screen
 */
sealed interface GamesUiState {
    data object Loading : GamesUiState
    data object Empty : GamesUiState
    data class Success(val games: List<Game>) : GamesUiState
    data class Error(val message: String) : GamesUiState
}

/**
 * UI state for the Game detail screen
 */
sealed interface GameDetailUiState {
    data object Loading : GameDetailUiState
    data class Success(val game: Game) : GameDetailUiState
    data class Error(val message: String) : GameDetailUiState
}
