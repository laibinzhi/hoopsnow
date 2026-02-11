package com.hoopsnow.nba.feature.games.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoopsnow.nba.core.designsystem.component.EmptyScreen
import com.hoopsnow.nba.core.designsystem.component.ErrorScreen
import com.hoopsnow.nba.core.designsystem.component.GameCard
import com.hoopsnow.nba.core.designsystem.component.LoadingScreen
import com.hoopsnow.nba.core.designsystem.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onGameClick: (Int) -> Unit,
    viewModel: GamesListViewModel = hiltViewModel(),
) {
    val selectedDateIndex by viewModel.selectedDateIndex.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    // Generate display dates (US Eastern Time)
    val displayDates = remember {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"))
        cal.add(Calendar.DAY_OF_YEAR, -3)
        (0 until 7).map {
            val date = cal.time.clone() as java.util.Date
            cal.add(Calendar.DAY_OF_YEAR, 1)
            date
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950),
    ) {
        // Header with status bar padding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
        ) {
            Text(
                text = "Games",
                color = White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
            )
        }

        HorizontalDivider(color = Slate800, thickness = 1.dp)

        // Date strip
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            itemsIndexed(displayDates) { index, date ->
                val isSelected = index == selectedDateIndex
                val isToday = index == 3
                val dayFormat = SimpleDateFormat("EEE", Locale.US)
                val dayName = if (isToday) "TODAY" else dayFormat.format(date).uppercase()
                val cal = Calendar.getInstance().apply { time = date }
                val dayNum = cal.get(Calendar.DAY_OF_MONTH)

                Column(
                    modifier = Modifier
                        .width(50.dp)
                        .height(56.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Blue600 else Slate800)
                        .then(
                            if (isSelected) Modifier.shadow(8.dp, CircleShape)
                            else Modifier
                        )
                        .clickable { viewModel.selectDate(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = dayName,
                        color = if (isSelected) White else if (isToday) Blue400 else Slate400,
                        fontSize = if (isToday) 9.sp else 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = if (isToday) 0.sp else 0.5.sp,
                    )
                    Text(
                        text = "$dayNum",
                        color = if (isSelected) White else Slate400,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        HorizontalDivider(color = Slate800, thickness = 1.dp)

        // Games content based on state
        when (val state = uiState) {
            is GamesUiState.Loading -> {
                LoadingScreen()
            }
            else -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (state) {
                        is GamesUiState.Empty -> {
                            EmptyScreen(message = "No games scheduled for this date")
                        }
                        is GamesUiState.Error -> {
                            ErrorScreen(message = state.message)
                        }
                        is GamesUiState.Success -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                            ) {
                                items(state.games, key = { it.id }) { game ->
                                    GameCard(
                                        game = game,
                                        onClick = { onGameClick(game.id) },
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
