package com.hoopsnow.nba.feature.players.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    private val playersRepository: PlayersRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    val favoritePlayerIds: StateFlow<Set<Int>> = favoritesRepository.favoritePlayerIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private val _uiState = MutableStateFlow<PlayerDetailUiState>(PlayerDetailUiState.Loading)
    val uiState: StateFlow<PlayerDetailUiState> = _uiState.asStateFlow()

    fun loadPlayer(playerId: Int) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            favoritesRepository.togglePlayerFavorite(playerId)
        }
    }
}
