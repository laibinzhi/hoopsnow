package com.hoopsnow.nba.feature.teams.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.GamesRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.data.repository.TeamsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailViewModel @Inject constructor(
    private val teamsRepository: TeamsRepository,
    private val gamesRepository: GamesRepository,
    private val playersRepository: PlayersRepository,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    val favoriteTeamIds: StateFlow<Set<Int>> = favoritesRepository.favoriteTeamIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    private val _uiState = MutableStateFlow<TeamDetailUiState>(TeamDetailUiState.Loading)
    val uiState: StateFlow<TeamDetailUiState> = _uiState.asStateFlow()

    fun loadTeam(teamId: Int) {
        viewModelScope.launch {
            _uiState.value = TeamDetailUiState.Loading
            try {
                combine(
                    teamsRepository.getTeamById(teamId).filterNotNull(),
                    playersRepository.getPlayersByTeamId(teamId),
                    gamesRepository.getGamesByTeamId(teamId),
                ) { team, players, games ->
                    TeamDetailUiState.Success(
                        team = team,
                        players = players,
                        recentGames = games.take(5),
                    )
                }
                    .catch { e ->
                        _uiState.value = TeamDetailUiState.Error(e.message ?: "Unknown error")
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            } catch (e: Exception) {
                _uiState.value = TeamDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleFavorite(teamId: Int) {
        viewModelScope.launch {
            favoritesRepository.toggleTeamFavorite(teamId)
        }
    }
}
