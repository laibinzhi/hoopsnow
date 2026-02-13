package com.hoopsnow.nba.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.ui.theme.*

@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isFinal = game.status.contains("Final")
    val isScheduled = !isFinal && game.homeTeamScore == 0 && game.visitorTeamScore == 0
    val isLive = !isFinal && !isScheduled
    val homeWin = isFinal && game.homeTeamScore > game.visitorTeamScore
    val awayWin = isFinal && game.visitorTeamScore > game.homeTeamScore

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(8.dp, RoundedCornerShape(12.dp), ambientColor = Slate950)
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() },
                )
            }
            .padding(16.dp),
    ) {
        Column {
            // Status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLive) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Red500, CircleShape),
                        )
                        Text(
                            text = " LIVE",
                            color = Red500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    } else {
                        Text(
                            text = if (isFinal) "FINAL" else "SCHEDULED",
                            color = Slate500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                        )
                    }
                }
                Text(
                    text = game.date.take(10),
                    color = Slate500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Teams and scores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Visitor team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    TeamLogo(
                        teamFullName = game.visitorTeam.fullName,
                        size = 36.dp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = game.visitorTeam.abbreviation,
                        color = if (awayWin && isFinal) White else Slate300,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = game.visitorTeam.city,
                        color = Slate500,
                        fontSize = 10.sp,
                    )
                    if (awayWin) {
                        Text(
                            text = "WIN",
                            color = Blue400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                // Score
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "${game.visitorTeamScore}",
                        color = when {
                            isLive -> White
                            isScheduled -> Slate500
                            awayWin -> White
                            else -> Slate500.copy(alpha = 0.6f)
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = "-",
                        color = Slate600,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "${game.homeTeamScore}",
                        color = when {
                            isLive -> White
                            isScheduled -> Slate500
                            homeWin -> White
                            else -> Slate500.copy(alpha = 0.6f)
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                    )
                }

                // Home team
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    TeamLogo(
                        teamFullName = game.homeTeam.fullName,
                        size = 36.dp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = game.homeTeam.abbreviation,
                        color = if (homeWin && isFinal) White else Slate300,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = game.homeTeam.city,
                        color = Slate500,
                        fontSize = 10.sp,
                    )
                    if (homeWin) {
                        Text(
                            text = "WIN",
                            color = Blue400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
