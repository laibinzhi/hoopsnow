package com.hoopsnow.nba.feature.players.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.common.result.NbaException
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.model.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 100

@HiltViewModel
class PlayersListViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayersUiState>(PlayersUiState.Loading)
    val uiState: StateFlow<PlayersUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var currentPlayers = mutableListOf<Player>()
    private var currentCursor: Int? = null

    init {
        loadFirstPage()
    }

    fun refresh() {
        loadFirstPage(isRefresh = true)
    }

    private fun loadFirstPage(isRefresh: Boolean = false) {
        loadJob?.cancel()

        val previousState = _uiState.value
        val previousPlayers = if (previousState is PlayersUiState.Success) previousState.players else emptyList()

        if (isRefresh) {
            if (previousState is PlayersUiState.Success) {
                _uiState.value = previousState.copy(isRefreshing = true)
            }
        } else {
            currentPlayers.clear()
            currentCursor = null
            _uiState.value = PlayersUiState.Loading
        }

        loadJob = viewModelScope.launch {
            // First, try to load from local database cache
            if (!isRefresh) {
                try {
                    val cachedPlayers = playersRepository.getPlayers().first()

                    if (cachedPlayers.isNotEmpty()) {
                        currentPlayers.clear()
                        currentPlayers.addAll(cachedPlayers)
                        _uiState.value = PlayersUiState.Success(
                            players = currentPlayers.toList(),
                            isRefreshing = false,
                            loadMoreState = LoadMoreState.Idle,
                            nextCursor = null,
                            isFromCache = true,
                        )
                    }
                } catch (e: Exception) {
                    // Ignore cache errors, continue to network
                }
            }

            // Then, load from network
            try {
                val result = playersRepository.loadPlayersPage(
                    cursor = null,
                    perPage = PAGE_SIZE,
                    search = null,
                )

                currentPlayers.clear()
                currentPlayers.addAll(result.players)
                currentCursor = result.nextCursor

                _uiState.value = if (currentPlayers.isEmpty()) {
                    PlayersUiState.Empty
                } else {
                    PlayersUiState.Success(
                        players = currentPlayers.toList(),
                        isRefreshing = false,
                        loadMoreState = if (result.hasMore) LoadMoreState.Idle else LoadMoreState.NoMore,
                        nextCursor = result.nextCursor,
                        isFromCache = false,
                    )
                }
            } catch (e: NbaException.RateLimitException) {
                // Rate limit: keep previous/cached data, show rate limit state
                val currentState = _uiState.value
                if (currentState is PlayersUiState.Success) {
                    _uiState.value = currentState.copy(
                        isRefreshing = false,
                        loadMoreState = LoadMoreState.RateLimit,
                    )
                } else if (previousPlayers.isNotEmpty()) {
                    _uiState.value = PlayersUiState.Success(
                        players = previousPlayers,
                        isRefreshing = false,
                        loadMoreState = LoadMoreState.RateLimit,
                    )
                } else {
                    _uiState.value = PlayersUiState.Error("Too many requests, please try again later")
                }
            } catch (e: Exception) {
                // Network error: keep cached data if available
                val currentState = _uiState.value
                if (currentState is PlayersUiState.Success) {
                    _uiState.value = currentState.copy(
                        isRefreshing = false,
                        loadMoreState = LoadMoreState.Error(e.message ?: "Network error"),
                    )
                } else if (isRefresh && previousPlayers.isNotEmpty()) {
                    _uiState.value = PlayersUiState.Success(
                        players = previousPlayers,
                        isRefreshing = false,
                        loadMoreState = LoadMoreState.Error(e.message ?: "Refresh failed"),
                    )
                } else {
                    _uiState.value = PlayersUiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState !is PlayersUiState.Success) return

        // Only allow load more when idle or error/rateLimit (for retry)
        when (currentState.loadMoreState) {
            is LoadMoreState.Loading, is LoadMoreState.NoMore -> return
            else -> {}
        }

        _uiState.value = currentState.copy(loadMoreState = LoadMoreState.Loading)

        viewModelScope.launch {
            try {
                val result = playersRepository.loadPlayersPage(
                    cursor = currentCursor,
                    perPage = PAGE_SIZE,
                    search = null,
                )

                currentPlayers.addAll(result.players)
                currentCursor = result.nextCursor

                _uiState.value = PlayersUiState.Success(
                    players = currentPlayers.toList(),
                    isRefreshing = false,
                    loadMoreState = if (result.hasMore) LoadMoreState.Idle else LoadMoreState.NoMore,
                    nextCursor = result.nextCursor,
                )
            } catch (e: NbaException.RateLimitException) {
                _uiState.value = currentState.copy(
                    loadMoreState = LoadMoreState.RateLimit,
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    loadMoreState = LoadMoreState.Error(e.message ?: "Load failed"),
                )
            }
        }
    }
}
