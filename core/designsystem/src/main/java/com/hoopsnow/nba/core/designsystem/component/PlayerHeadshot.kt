package com.hoopsnow.nba.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.hoopsnow.nba.core.designsystem.theme.Slate500
import com.hoopsnow.nba.core.designsystem.theme.Slate600
import com.hoopsnow.nba.core.designsystem.theme.Slate700
import com.hoopsnow.nba.core.designsystem.theme.Slate800

/**
 * CompositionLocal that provides a function to get player headshot URL by player name.
 * Must be provided at the app level via CompositionLocalProvider.
 */
val LocalPlayerHeadshot = staticCompositionLocalOf<(String, String) -> String?> { { _, _ -> null } }

/**
 * Displays a player headshot image loaded from NBA CDN.
 * Shows a placeholder while loading and falls back to initials avatar on error.
 */
@Composable
fun PlayerHeadshot(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val getHeadshotUrl = LocalPlayerHeadshot.current
    val headshotUrl = getHeadshotUrl(firstName, lastName)

    if (headshotUrl != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(headshotUrl)
                .crossfade(true)
                .build(),
            contentDescription = "$firstName $lastName",
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            loading = {
                PlayerPlaceholder(size = size)
            },
            error = {
                PlayerInitialsAvatar(
                    firstName = firstName,
                    lastName = lastName,
                    size = size,
                )
            },
        )
    } else {
        PlayerInitialsAvatar(
            firstName = firstName,
            lastName = lastName,
            modifier = modifier,
            size = size,
        )
    }
}

/**
 * Loading placeholder with person icon.
 */
@Composable
fun PlayerPlaceholder(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Slate800, CircleShape)
            .border(1.dp, Slate700, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Slate600,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}

/**
 * Fallback avatar showing player initials.
 */
@Composable
fun PlayerInitialsAvatar(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Slate800, CircleShape)
            .border(1.dp, Slate700, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "${firstName.firstOrNull() ?: "?"}${lastName.firstOrNull() ?: "?"}",
            color = Slate500,
            fontSize = (size.value / 2.5).sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
