package com.hoopsnow.nba.feature.favorites.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.data.repository.TeamsRepository
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoritesListViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val playersRepository: PlayersRepository,
    private val teamsRepository: TeamsRepository,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow("PLAYERS")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    val favoritePlayerIds: StateFlow<Set<Int>> = favoritesRepository.favoritePlayerIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    val favoriteTeamIds: StateFlow<Set<Int>> = favoritesRepository.favoriteTeamIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet(),
        )

    // Combine favorite IDs with actual data
    val uiState: StateFlow<FavoritesUiState> = combine(
        favoritesRepository.favoritePlayerIds,
        favoritesRepository.favoriteTeamIds,
    ) { playerIds, teamIds ->
        Pair(playerIds, teamIds)
    }.flatMapLatest { (playerIds, teamIds) ->
        if (playerIds.isEmpty() && teamIds.isEmpty()) {
            flowOf(FavoritesUiState.Empty)
        } else {
            combine(
                if (playerIds.isNotEmpty()) {
                    playersRepository.getPlayers().map { players ->
                        players.filter { it.id in playerIds }
                    }
                } else {
                    flowOf(emptyList())
                },
                if (teamIds.isNotEmpty()) {
                    teamsRepository.getTeams().map { teams ->
                        teams.filter { it.id in teamIds }
                    }
                } else {
                    flowOf(emptyList())
                },
            ) { players, teams ->
                FavoritesUiState.Success(
                    players = players,
                    teams = teams,
                )
            }
        }
    }
        .catch { e ->
            emit(FavoritesUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState.Loading,
        )

    fun switchTab(tab: String) {
        _selectedTab.value = tab
    }

    fun getFavoritePlayers(allPlayers: List<Player>): List<Player> =
        allPlayers.filter { it.id in favoritePlayerIds.value }

    fun getFavoriteTeams(allTeams: List<Team>): List<Team> =
        allTeams.filter { it.id in favoriteTeamIds.value }
}
