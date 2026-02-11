package com.hoopsnow.nba.feature.players.impl

import com.hoopsnow.nba.core.model.Player

/**
 * Load more state for pagination
 */
sealed interface LoadMoreState {
    data object Idle : LoadMoreState
    data object Loading : LoadMoreState
    data object NoMore : LoadMoreState
    data class Error(val message: String) : LoadMoreState
    data object RateLimit : LoadMoreState
}

/**
 * UI state for the Players list screen
 */
sealed interface PlayersUiState {
    data object Loading : PlayersUiState
    data object Empty : PlayersUiState
    data class Success(
        val players: List<Player>,
        val isRefreshing: Boolean = false,
        val loadMoreState: LoadMoreState = LoadMoreState.Idle,
        val nextCursor: Int? = null,
        val isFromCache: Boolean = false,
    ) : PlayersUiState {
        val hasMore: Boolean
            get() = loadMoreState !is LoadMoreState.NoMore
        val isLoadingMore: Boolean
            get() = loadMoreState is LoadMoreState.Loading
        val canRetryLoadMore: Boolean
            get() = loadMoreState is LoadMoreState.Error || loadMoreState is LoadMoreState.RateLimit
    }
    data class Error(val message: String) : PlayersUiState
}

/**
 * UI state for the Player detail screen
 */
sealed interface PlayerDetailUiState {
    data object Loading : PlayerDetailUiState
    data class Success(val player: Player) : PlayerDetailUiState
    data class Error(val message: String) : PlayerDetailUiState
}
