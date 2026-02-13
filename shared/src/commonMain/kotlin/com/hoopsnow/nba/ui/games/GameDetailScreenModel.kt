package com.hoopsnow.nba.ui.games

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoopsnow.nba.core.data.repository.GamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class GameDetailScreenModel(
    private val gamesRepository: GamesRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    fun loadGame(gameId: Int) {
        screenModelScope.launch {
            _uiState.value = GameDetailUiState.Loading
            gamesRepository.getGameById(gameId)
                .filterNotNull()
                .catch { e ->
                    _uiState.value = GameDetailUiState.Error(e.message ?: "Unknown error")
                }
                .collect { game ->
                    _uiState.value = GameDetailUiState.Success(game)
                }
        }
    }
}
