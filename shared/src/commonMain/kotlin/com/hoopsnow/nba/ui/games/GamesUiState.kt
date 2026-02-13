package com.hoopsnow.nba.ui.games

import com.hoopsnow.nba.core.model.Game

sealed interface GamesUiState {
    data object Loading : GamesUiState
    data object Empty : GamesUiState
    data class Success(val games: List<Game>) : GamesUiState
    data class Error(val message: String) : GamesUiState
}

sealed interface GameDetailUiState {
    data object Loading : GameDetailUiState
    data class Success(val game: Game) : GameDetailUiState
    data class Error(val message: String) : GameDetailUiState
}
