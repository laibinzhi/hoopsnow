package com.hoopsnow.nba.feature.favorites.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoopsnow.nba.core.designsystem.component.EmptyScreen
import com.hoopsnow.nba.core.designsystem.component.ErrorScreen
import com.hoopsnow.nba.core.designsystem.component.LoadingScreen
import com.hoopsnow.nba.core.designsystem.component.PlayerHeadshot
import com.hoopsnow.nba.core.designsystem.component.TeamLogo
import com.hoopsnow.nba.core.designsystem.theme.*
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

@Composable
fun FavoritesListScreen(
    onPlayerClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit,
    viewModel: FavoritesListViewModel = hiltViewModel(),
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        ) {
            Text(
                text = "Favorites",
                color = White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            // Tab bar
            Row {
                listOf("PLAYERS" to "Players", "TEAMS" to "Teams").forEach { (key, label) ->
                    val isActive = selectedTab == key
                    Column(
                        modifier = Modifier
                            .clickable { viewModel.switchTab(key) }
                            .padding(end = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = label,
                            color = if (isActive) Blue500 else Slate500,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                        if (isActive) {
                            HorizontalDivider(
                                color = Blue500,
                                thickness = 2.dp,
                                modifier = Modifier.width(60.dp),
                            )
                        } else {
                            Spacer(modifier = Modifier.size(2.dp))
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Slate800, thickness = 1.dp)

        // Content based on state
        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                LoadingScreen()
            }
            is FavoritesUiState.Empty -> {
                EmptyScreen(message = "No favorites yet. Add players or teams to your favorites!")
            }
            is FavoritesUiState.Error -> {
                ErrorScreen(message = state.message)
            }
            is FavoritesUiState.Success -> {
                if (selectedTab == "PLAYERS") {
                    if (state.players.isEmpty()) {
                        EmptyScreen(message = "No favorite players yet.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(state.players, key = { it.id }) { player ->
                                FavoritePlayerItem(player, onClick = { onPlayerClick(player.id) })
                            }
                        }
                    }
                } else {
                    if (state.teams.isEmpty()) {
                        EmptyScreen(message = "No favorite teams yet.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(state.teams, key = { it.id }) { team ->
                                FavoriteTeamItem(team, onClick = { onTeamClick(team.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoritePlayerItem(player: Player, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerHeadshot(
            firstName = player.firstName,
            lastName = player.lastName,
            size = 40.dp,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text("${player.firstName} ${player.lastName}", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            val info = buildString {
                if (player.position.isNotBlank()) append(player.position)
                player.team?.let { team ->
                    if (isNotEmpty()) append(" · ")
                    append(team.abbreviation)
                }
            }
            if (info.isNotBlank()) {
                Text(info, color = Slate400, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FavoriteTeamItem(team: Team, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TeamLogo(
            teamFullName = team.fullName,
            size = 40.dp,
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(team.name, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(team.city, color = Slate400, fontSize = 12.sp)
        }
    }
}
