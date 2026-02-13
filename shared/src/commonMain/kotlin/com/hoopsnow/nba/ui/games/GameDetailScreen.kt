package com.hoopsnow.nba.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.ui.component.ErrorScreen
import com.hoopsnow.nba.ui.component.LoadingScreen
import com.hoopsnow.nba.ui.component.TeamLogo
import com.hoopsnow.nba.ui.teams.TeamDetailScreen
import com.hoopsnow.nba.ui.theme.*

data class GameDetailScreen(val gameId: Int) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<GameDetailScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        LaunchedEffect(gameId) {
            screenModel.loadGame(gameId)
        }

        when (val state = uiState) {
            is GameDetailUiState.Loading -> LoadingScreen()
            is GameDetailUiState.Error -> ErrorScreen(message = state.message)
            is GameDetailUiState.Success -> {
                GameDetailContent(
                    game = state.game,
                    onBack = { navigator.pop() },
                    onTeamClick = { teamId -> navigator.push(TeamDetailScreen(teamId)) },
                )
            }
        }
    }
}

@Composable
private fun GameDetailContent(
    game: Game,
    onBack: () -> Unit,
    onTeamClick: (Int) -> Unit,
) {
    val isFinal = game.status.contains("Final")
    val isLive = !isFinal && game.status != "Scheduled"
    val homeWin = isFinal && game.homeTeamScore > game.visitorTeamScore
    val awayWin = isFinal && game.visitorTeamScore > game.homeTeamScore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        // Header + Scoreboard
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900),
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Slate400, modifier = Modifier.size(24.dp))
                }
                Text(
                    text = if (isLive) "● LIVE" else game.status,
                    color = if (isLive) Red500 else Slate500,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(36.dp))
            }

            // Scoreboard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Visitor
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TeamLogo(teamFullName = game.visitorTeam.fullName, size = 64.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(game.visitorTeam.name, color = if (awayWin) White else Slate300, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Visitor", color = Slate500, fontSize = 12.sp)
                }

                Spacer(Modifier.width(24.dp))

                // Score
                Text("${game.visitorTeamScore}", color = if (awayWin || isLive) White else Slate500, fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
                Text(":", color = Slate600, fontSize = 24.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp))
                Text("${game.homeTeamScore}", color = if (homeWin || isLive) White else Slate500, fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)

                Spacer(Modifier.width(24.dp))

                // Home
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TeamLogo(teamFullName = game.homeTeam.fullName, size = 64.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(game.homeTeam.name, color = if (homeWin) White else Slate300, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Home", color = Slate500, fontSize = 12.sp)
                }
            }
        }

        HorizontalDivider(color = Slate800, thickness = 1.dp)

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            GameInfoCard(game)
            Spacer(Modifier.height(24.dp))
            TeamButton("View ${game.visitorTeam.name} Details") { onTeamClick(game.visitorTeam.id) }
            Spacer(Modifier.height(12.dp))
            TeamButton("View ${game.homeTeam.name} Details") { onTeamClick(game.homeTeam.id) }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun GameInfoCard(game: Game) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate800.copy(alpha = 0.5f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text("GAME INFO", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        }
        HorizontalDivider(color = Slate800, thickness = 1.dp)
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow("Date", game.date.take(10))
            Spacer(Modifier.height(8.dp))
            InfoRow("Season", "${game.season}-${game.season + 1}")
            Spacer(Modifier.height(8.dp))
            InfoRow("Status", game.status)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Slate500, fontSize = 14.sp)
        Text(value, color = White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TeamButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(2.dp, Slate700, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Slate300, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
