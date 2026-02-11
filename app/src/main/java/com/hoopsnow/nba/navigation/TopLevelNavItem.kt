package com.hoopsnow.nba.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.hoopsnow.nba.feature.favorites.api.FavoritesNavKey
import com.hoopsnow.nba.feature.games.api.GamesNavKey
import com.hoopsnow.nba.feature.players.api.PlayersNavKey
import com.hoopsnow.nba.feature.teams.api.TeamsNavKey

/**
 * Type for the top level navigation items in the application.
 */
data class TopLevelNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val TOP_LEVEL_NAV_ITEMS: Map<NavKey, TopLevelNavItem> = mapOf(
    GamesNavKey to TopLevelNavItem(
        label = "Games",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange,
    ),
    TeamsNavKey to TopLevelNavItem(
        label = "Teams",
        selectedIcon = Icons.Outlined.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
    ),
    PlayersNavKey to TopLevelNavItem(
        label = "Players",
        selectedIcon = Icons.Outlined.People,
        unselectedIcon = Icons.Outlined.People,
    ),
    FavoritesNavKey to TopLevelNavItem(
        label = "Favorites",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.StarBorder,
    ),
)
