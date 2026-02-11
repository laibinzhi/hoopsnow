package com.hoopsnow.nba.feature.teams.impl

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoopsnow.nba.core.designsystem.component.ErrorScreen
import com.hoopsnow.nba.core.designsystem.component.GameCard
import com.hoopsnow.nba.core.designsystem.component.HoopsTopBar
import com.hoopsnow.nba.core.designsystem.component.LoadingScreen
import com.hoopsnow.nba.core.designsystem.component.PlayerHeadshot
import com.hoopsnow.nba.core.designsystem.component.TeamLogo
import com.hoopsnow.nba.core.designsystem.theme.*
import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team

private enum class TeamDetailTab {
    ROSTER,
    GAMES,
}

@Composable
fun TeamDetailScreen(
    teamId: Int,
    onBack: () -> Unit,
    onGameClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit = {},
    viewModel: TeamDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteTeamIds.collectAsStateWithLifecycle()

    LaunchedEffect(teamId) {
        viewModel.loadTeam(teamId)
    }

    when (val state = uiState) {
        is TeamDetailUiState.Loading -> {
            LoadingScreen()
        }
        is TeamDetailUiState.Error -> {
            ErrorScreen(message = state.message)
        }
        is TeamDetailUiState.Success -> {
            TeamDetailContent(
                team = state.team,
                players = state.players,
                recentGames = state.recentGames,
                isFavorite = teamId in favoriteIds,
                onBack = onBack,
                onGameClick = onGameClick,
                onPlayerClick = onPlayerClick,
                onToggleFavorite = { viewModel.toggleFavorite(teamId) },
            )
        }
    }
}

@Composable
private fun TeamDetailContent(
    team: Team,
    players: List<Player>,
    recentGames: List<Game>,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onGameClick: (Int) -> Unit,
    onPlayerClick: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(TeamDetailTab.ROSTER) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        HoopsTopBar(
            title = team.name,
            onBack = onBack,
            rightAction = {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Yellow400 else Slate600,
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
        )

        HorizontalDivider(color = Slate800, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Hero section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Slate900, Slate950))
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TeamLogo(
                        teamFullName = team.fullName,
                        size = 96.dp,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(team.city, color = White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text(team.name, color = Blue500, fontSize = 30.sp, fontWeight = FontWeight.Black)
                }
            }

            item {
                HorizontalDivider(color = Slate800, thickness = 1.dp)
            }

            // Info grid
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InfoCard("Conference", team.conference, Modifier.weight(1f))
                    InfoCard("Division", team.division, Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InfoCard("City", team.city, Modifier.weight(1f))
                    InfoCard("Abbr", team.abbreviation, Modifier.weight(1f))
                }
            }

            // Tab bar
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        TabItem(
                            title = "Roster",
                            isSelected = selectedTab == TeamDetailTab.ROSTER,
                            onClick = { selectedTab = TeamDetailTab.ROSTER },
                        )
                        Spacer(Modifier.width(24.dp))
                        TabItem(
                            title = "Recent Games",
                            isSelected = selectedTab == TeamDetailTab.GAMES,
                            onClick = { selectedTab = TeamDetailTab.GAMES },
                        )
                    }
                    HorizontalDivider(color = Slate800, thickness = 1.dp)
                }
            }

            // Tab content
            when (selectedTab) {
                TeamDetailTab.ROSTER -> {
                    if (players.isEmpty()) {
                        item {
                            Text(
                                "No players found",
                                color = Slate500,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        items(players, key = { it.id }) { player ->
                            PlayerRosterItem(
                                player = player,
                                onClick = { onPlayerClick(player.id) },
                            )
                        }
                    }
                }
                TeamDetailTab.GAMES -> {
                    if (recentGames.isEmpty()) {
                        item {
                            Text(
                                "No recent games found",
                                color = Slate500,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        item { Spacer(Modifier.height(12.dp)) }
                        items(recentGames, key = { it.id }) { game ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                GameCard(game = game, onClick = { onGameClick(game.id) })
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            color = if (isSelected) Blue500 else Slate500,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        if (isSelected) {
            HorizontalDivider(
                color = Blue500,
                thickness = 2.dp,
                modifier = Modifier.width(if (title == "Roster") 50.dp else 100.dp),
            )
        } else {
            Spacer(Modifier.height(2.dp))
        }
    }
}

@Composable
private fun PlayerRosterItem(
    player: Player,
    onClick: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                    onTap = { onClick() },
                )
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        PlayerHeadshot(
            firstName = player.firstName,
            lastName = player.lastName,
            size = 44.dp,
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${player.firstName} ${player.lastName}",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            if (player.position.isNotBlank()) {
                Text(
                    text = player.position,
                    color = Slate400,
                    fontSize = 12.sp,
                )
            }
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            tint = Slate600,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(bottom = 12.dp)
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label.uppercase(), color = Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
