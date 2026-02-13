package com.hoopsnow.nba.ui.teams

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoopsnow.nba.core.data.repository.TeamsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TeamsListScreenModel(
    private val teamsRepository: TeamsRepository,
) : ScreenModel {

    private val _conference = MutableStateFlow("East")
    val conference: StateFlow<String> = _conference.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TeamsUiState> = _conference
        .flatMapLatest { conf ->
            teamsRepository.getTeams()
                .map { teams ->
                    val filtered = teams.filter { it.conference.equals(conf, ignoreCase = true) }
                    if (filtered.isEmpty()) {
                        TeamsUiState.Empty
                    } else {
                        TeamsUiState.Success(filtered)
                    }
                }
                .catch { e ->
                    emit(TeamsUiState.Error(e.message ?: "Unknown error"))
                }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TeamsUiState.Loading,
        )

    fun switchConference(conf: String) {
        _conference.value = conf
    }
}
