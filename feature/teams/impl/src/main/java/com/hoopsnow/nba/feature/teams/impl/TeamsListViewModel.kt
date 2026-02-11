package com.hoopsnow.nba.feature.teams.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.data.repository.TeamsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TeamsListViewModel @Inject constructor(
    private val teamsRepository: TeamsRepository,
) : ViewModel() {

    private val _conference = MutableStateFlow("East")
    val conference: StateFlow<String> = _conference.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TeamsUiState> = _conference
        .flatMapLatest { conf ->
            teamsRepository.getTeamsByConference(conf)
                .map { teams ->
                    if (teams.isEmpty()) {
                        TeamsUiState.Empty
                    } else {
                        TeamsUiState.Success(teams)
                    }
                }
                .catch { e ->
                    emit(TeamsUiState.Error(e.message ?: "Unknown error"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TeamsUiState.Loading,
        )

    fun switchConference(conf: String) {
        _conference.value = conf
    }
}
