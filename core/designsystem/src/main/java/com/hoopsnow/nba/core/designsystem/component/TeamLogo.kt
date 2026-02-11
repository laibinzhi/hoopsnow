package com.hoopsnow.nba.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoopsnow.nba.core.designsystem.theme.Slate500
import com.hoopsnow.nba.core.designsystem.theme.Slate700
import com.hoopsnow.nba.core.designsystem.theme.Slate800

/**
 * CompositionLocal that provides team fullName -> logo URL mapping.
 * Must be provided at the app level via CompositionLocalProvider.
 */
val LocalTeamLogos = staticCompositionLocalOf<Map<String, String>> { emptyMap() }

/**
 * Displays a team logo image loaded from a remote URL.
 * Falls back to a text avatar with the first character of the team name when no logo is available.
 */
@Composable
fun TeamLogo(
    teamFullName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val logoMap = LocalTeamLogos.current
    val logoUrl = logoMap[teamFullName]

    if (logoUrl != null) {
        AsyncImage(
            model = logoUrl,
            contentDescription = teamFullName,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Fit,
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .background(Slate800, CircleShape)
                .border(1.dp, Slate700, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = teamFullName.firstOrNull()?.toString() ?: "?",
                color = Slate500,
                fontWeight = FontWeight.Black,
            )
        }
    }
}
