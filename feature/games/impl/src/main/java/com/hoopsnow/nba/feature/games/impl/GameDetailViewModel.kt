package com.hoopsnow.nba.feature.games.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.data.repository.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gamesRepository: GamesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    fun loadGame(gameId: Int) {
        viewModelScope.launch {
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
