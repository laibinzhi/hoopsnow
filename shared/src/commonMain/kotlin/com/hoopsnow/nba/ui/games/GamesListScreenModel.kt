package com.hoopsnow.nba.ui.games

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.hoopsnow.nba.core.data.repository.GamesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class GamesListScreenModel(
    private val gamesRepository: GamesRepository,
) : ScreenModel {

    private val _selectedDateIndex = MutableStateFlow(3) // Today in center
    val selectedDateIndex: StateFlow<Int> = _selectedDateIndex.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Generate dates for the date strip (US Eastern Time)
    val dates: List<String> = run {
        val tz = TimeZone.of("America/New_York")
        val today = Clock.System.now().toLocalDateTime(tz).date
        (-3..3).map { offset ->
            val date = if (offset < 0) {
                today.minus(-offset, DateTimeUnit.DAY)
            } else {
                today.plus(offset, DateTimeUnit.DAY)
            }
            date.toString() // yyyy-MM-dd format
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<GamesUiState> = _selectedDateIndex
        .flatMapLatest { index ->
            val selectedDate = dates[index]
            flow<GamesUiState> {
                emit(GamesUiState.Loading)
                emitAll(
                    gamesRepository.getGamesByDate(selectedDate)
                        .map { games ->
                            if (games.isEmpty()) {
                                GamesUiState.Empty
                            } else {
                                GamesUiState.Success(games)
                            }
                        }
                        .catch { e ->
                            emit(GamesUiState.Error(e.message ?: "Unknown error"))
                        }
                )
            }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GamesUiState.Loading,
        )

    fun selectDate(index: Int) {
        _selectedDateIndex.value = index
    }

    fun refresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
            try {
                gamesRepository.syncGamesByDate(dates[_selectedDateIndex.value])
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
