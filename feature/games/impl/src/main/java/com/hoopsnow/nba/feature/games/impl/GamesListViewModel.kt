package com.hoopsnow.nba.feature.games.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoopsnow.nba.core.data.repository.GamesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class GamesListViewModel @Inject constructor(
    private val gamesRepository: GamesRepository,
) : ViewModel() {

    private val _selectedDateIndex = MutableStateFlow(3) // Today in center
    val selectedDateIndex: StateFlow<Int> = _selectedDateIndex.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Generate dates for the date strip (US Eastern Time)
    val dates: List<String> = run {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("America/New_York")
        }
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        cal.add(Calendar.DAY_OF_YEAR, -3)
        (0 until 7).map {
            val date = dateFormat.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
            date
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
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GamesUiState.Loading,
        )

    fun selectDate(index: Int) {
        _selectedDateIndex.value = index
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                gamesRepository.syncGamesByDate(dates[_selectedDateIndex.value])
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
