package com.hoopsnow.nba.ui.players

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoopsnow.nba.core.common.NbaException
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.model.Player
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 100

@OptIn(FlowPreview::class)
class PlayerSearchScreenModel(
    private val playersRepository: PlayersRepository,
) : ScreenModel {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<PlayerSearchUiState>(PlayerSearchUiState.Idle)
    val uiState: StateFlow<PlayerSearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var currentPlayers = mutableListOf<Player>()
    private var currentCursor: Int? = null

    init {
        _searchQuery
            .debounce(300)
            .onEach { query ->
                if (query.isBlank()) {
                    _uiState.value = PlayerSearchUiState.Idle
                } else {
                    search(query)
                }
            }
            .launchIn(screenModelScope)
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
        currentPlayers.clear()
        currentCursor = null
        _uiState.value = PlayerSearchUiState.Idle
    }

    fun performSearch() {
        val query = _searchQuery.value
        if (query.isNotBlank()) {
            search(query)
        }
    }

    private fun search(query: String) {
        searchJob?.cancel()
        currentPlayers.clear()
        currentCursor = null
        _uiState.value = PlayerSearchUiState.Loading

        searchJob = screenModelScope.launch {
            try {
                val result = playersRepository.loadPlayersPage(
                    cursor = null,
                    perPage = PAGE_SIZE,
                    search = query,
                )

                currentPlayers.addAll(result.players)
                currentCursor = result.nextCursor

                _uiState.value = if (currentPlayers.isEmpty()) {
                    PlayerSearchUiState.Empty
                } else {
                    PlayerSearchUiState.Success(
                        players = currentPlayers.toList(),
                        loadMoreState = if (result.hasMore) LoadMoreState.Idle else LoadMoreState.NoMore,
                        nextCursor = result.nextCursor,
                    )
                }
            } catch (e: NbaException.RateLimitException) {
                _uiState.value = PlayerSearchUiState.Error("Too many requests, please try again later")
            } catch (e: Exception) {
                _uiState.value = PlayerSearchUiState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun loadMore() {
        val currentState = _uiState.value
        if (currentState !is PlayerSearchUiState.Success) return

        when (currentState.loadMoreState) {
            is LoadMoreState.Loading, is LoadMoreState.NoMore -> return
            else -> {}
        }

        _uiState.value = currentState.copy(loadMoreState = LoadMoreState.Loading)

        screenModelScope.launch {
            try {
                val result = playersRepository.loadPlayersPage(
                    cursor = currentCursor,
                    perPage = PAGE_SIZE,
                    search = _searchQuery.value,
                )

                currentPlayers.addAll(result.players)
                currentCursor = result.nextCursor

                _uiState.value = PlayerSearchUiState.Success(
                    players = currentPlayers.toList(),
                    loadMoreState = if (result.hasMore) LoadMoreState.Idle else LoadMoreState.NoMore,
                    nextCursor = result.nextCursor,
                )
            } catch (e: NbaException.RateLimitException) {
                _uiState.value = currentState.copy(loadMoreState = LoadMoreState.RateLimit)
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    loadMoreState = LoadMoreState.Error(e.message ?: "Load failed"),
                )
            }
        }
    }
}
