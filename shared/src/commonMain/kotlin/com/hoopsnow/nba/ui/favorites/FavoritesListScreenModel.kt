package com.hoopsnow.nba.ui.favorites

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.data.repository.TeamsRepository
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

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FavoritesListScreenModel(
    private val favoritesRepository: FavoritesRepository,
    private val playersRepository: PlayersRepository,
    private val teamsRepository: TeamsRepository,
) : ScreenModel {

    private val _selectedTab = MutableStateFlow("PLAYERS")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

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
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoritesUiState.Loading,
        )

    fun switchTab(tab: String) {
        _selectedTab.value = tab
    }
}
