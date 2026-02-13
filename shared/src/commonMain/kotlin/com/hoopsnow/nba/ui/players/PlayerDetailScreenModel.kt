package com.hoopsnow.nba.ui.players

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerDetailScreenModel(
    private val playersRepository: PlayersRepository,
    private val favoritesRepository: FavoritesRepository,
) : ScreenModel {

    val favoritePlayerIds: StateFlow<Set<Int>> = favoritesRepository.favoritePlayerIds
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private val _uiState = MutableStateFlow<PlayerDetailUiState>(PlayerDetailUiState.Loading)
    val uiState: StateFlow<PlayerDetailUiState> = _uiState.asStateFlow()

    fun loadPlayer(playerId: Int) {
        screenModelScope.launch {
            _uiState.value = PlayerDetailUiState.Loading
            playersRepository.getPlayerById(playerId)
                .filterNotNull()
                .catch { e ->
                    _uiState.value = PlayerDetailUiState.Error(e.message ?: "Unknown error")
                }
                .collect { player ->
                    _uiState.value = PlayerDetailUiState.Success(player)
                }
        }
    }

    fun toggleFavorite(playerId: Int) {
        screenModelScope.launch {
            favoritesRepository.togglePlayerFavorite(playerId)
        }
    }
}
