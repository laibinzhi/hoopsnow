package com.hoopsnow.nba.feature.players.impl

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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hoopsnow.nba.core.designsystem.component.ErrorScreen
import com.hoopsnow.nba.core.designsystem.component.HoopsTopBar
import com.hoopsnow.nba.core.designsystem.component.LoadingScreen
import com.hoopsnow.nba.core.designsystem.component.LocalPlayerHeadshot
import com.hoopsnow.nba.core.designsystem.theme.*
import com.hoopsnow.nba.core.model.Player

@Composable
fun PlayerDetailScreen(
    playerId: Int,
    onBack: () -> Unit,
    onTeamClick: (Int) -> Unit,
    viewModel: PlayerDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoritePlayerIds.collectAsStateWithLifecycle()

    LaunchedEffect(playerId) {
        viewModel.loadPlayer(playerId)
    }

    when (val state = uiState) {
        is PlayerDetailUiState.Loading -> {
            LoadingScreen()
        }
        is PlayerDetailUiState.Error -> {
            ErrorScreen(message = state.message)
        }
        is PlayerDetailUiState.Success -> {
            PlayerDetailContent(
                player = state.player,
                isFavorite = playerId in favoriteIds,
                onBack = onBack,
                onTeamClick = onTeamClick,
                onToggleFavorite = { viewModel.toggleFavorite(playerId) },
            )
        }
    }
}

@Composable
private fun PlayerDetailContent(
    player: Player,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onTeamClick: (Int) -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val team = player.team
    val getHeadshotUrl = LocalPlayerHeadshot.current
    val headshotUrl = getHeadshotUrl(player.firstName, player.lastName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        HoopsTopBar(
            title = "${player.firstName} ${player.lastName}",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp)
                    .background(Slate900),
            ) {
                // Background - Player headshot or initials
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(listOf(Slate900, Slate800))
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (headshotUrl != null) {
                        AsyncImage(
                            model = headshotUrl,
                            contentDescription = "${player.firstName} ${player.lastName}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(0.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Text(
                            text = "${player.firstName.firstOrNull() ?: ""}${player.lastName.firstOrNull() ?: ""}",
                            color = Slate950,
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                    }
                }

                // Gradient overlay + info
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Slate950.copy(alpha = 0.1f), Slate950),
                                startY = 0f,
                                endY = 700f,
                            )
                        )
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(player.firstName, color = White, fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp, lineHeight = 48.sp)
                    Text(player.lastName, color = White, fontSize = 48.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp, lineHeight = 48.sp)

                    if (player.position.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 12.dp),
                        ) {
                            Column {
                                Text("POSITION", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text(player.position, color = Blue500, fontSize = 30.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Slate800, thickness = 1.dp)

            // Current team button
            if (team != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900)
                        .clickable { onTeamClick(team.id) }
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("CURRENT TEAM", color = Blue500, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(team.fullName, color = White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Slate800, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, "Go", tint = White, modifier = Modifier.size(20.dp))
                    }
                }

                HorizontalDivider(color = Slate800, thickness = 1.dp)
            }

            // Player Info
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width(4.dp).height(24.dp).background(Red500, RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(8.dp))
                    Text("PLAYER INFO", color = White, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.3).sp)
                }

                Spacer(Modifier.height(16.dp))

                InfoRow("First Name", player.firstName)
                InfoRow("Last Name", player.lastName)
                if (player.position.isNotBlank()) {
                    InfoRow("Position", player.position)
                }
                team?.let {
                    InfoRow("Team", it.fullName)
                    InfoRow("Conference", it.conference)
                    InfoRow("Division", it.division)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label.uppercase(), color = Slate500, fontSize = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp)
        Text(value, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(color = Slate800, thickness = 1.dp)
}
