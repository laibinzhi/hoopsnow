package com.hoopsnow.nba.ui.favorites

import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data object Empty : FavoritesUiState
    data class Success(
        val players: List<Player>,
        val teams: List<Team>,
    ) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}
