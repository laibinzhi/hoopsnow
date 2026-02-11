package com.hoopsnow.nba

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.hoopsnow.nba.core.designsystem.theme.Blue400
import com.hoopsnow.nba.core.designsystem.theme.Slate600
import com.hoopsnow.nba.core.designsystem.theme.Slate800
import com.hoopsnow.nba.core.designsystem.theme.Slate900
import com.hoopsnow.nba.core.designsystem.theme.Slate950
import com.hoopsnow.nba.feature.games.api.GameDetailNavKey
import com.hoopsnow.nba.feature.games.api.GamesNavKey
import com.hoopsnow.nba.feature.players.api.PlayerDetailNavKey
import com.hoopsnow.nba.feature.players.api.PlayerSearchNavKey
import com.hoopsnow.nba.feature.teams.api.TeamDetailNavKey
import com.hoopsnow.nba.navigation.Navigator
import com.hoopsnow.nba.navigation.TOP_LEVEL_NAV_ITEMS
import com.hoopsnow.nba.navigation.favoritesEntry
import com.hoopsnow.nba.navigation.gameDetailEntry
import com.hoopsnow.nba.navigation.gamesEntry
import com.hoopsnow.nba.navigation.playerDetailEntry
import com.hoopsnow.nba.navigation.playerSearchEntry
import com.hoopsnow.nba.navigation.playersEntry
import com.hoopsnow.nba.navigation.rememberNavigationState
import com.hoopsnow.nba.navigation.teamDetailEntry
import com.hoopsnow.nba.navigation.teamsEntry
import com.hoopsnow.nba.navigation.toEntries

// Detail routes that should hide the bottom nav
private val detailNavKeys = setOf(
    GameDetailNavKey::class,
    TeamDetailNavKey::class,
    PlayerDetailNavKey::class,
    PlayerSearchNavKey::class,
)

@Composable
fun HoopsNowApp() {
    val navigationState = rememberNavigationState(
        startKey = GamesNavKey,
        topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys,
    )

    val navigator = remember { Navigator(navigationState) }

    // Check if current key is a detail screen
    val showBottomBar = navigationState.currentKey::class !in detailNavKeys

    Scaffold(
        containerColor = Slate950,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                Column {
                    HorizontalDivider(color = Slate800, thickness = 1.dp)
                    NavigationBar(
                        containerColor = Slate900,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    ) {
                        TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                            val selected = navKey == navigationState.currentTopLevelKey

                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigator.navigate(navKey) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) navItem.selectedIcon else navItem.unselectedIcon,
                                        contentDescription = navItem.label,
                                        modifier = Modifier.size(24.dp),
                                    )
                                },
                                label = {
                                    Text(
                                        text = navItem.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Blue400,
                                    selectedTextColor = Blue400,
                                    unselectedIconColor = Slate600,
                                    unselectedTextColor = Slate600,
                                    indicatorColor = Slate900,
                                ),
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val entryProvider = entryProvider<NavKey> {
                gamesEntry(navigator)
                gameDetailEntry(navigator)
                teamsEntry(navigator)
                teamDetailEntry(navigator)
                playersEntry(navigator)
                playerSearchEntry(navigator)
                playerDetailEntry(navigator)
                favoritesEntry(navigator)
            }

            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                transitionSpec = {
                    val duration = 400
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(duration, easing = LinearOutSlowInEasing),
                    ) + fadeIn(
                        animationSpec = tween(duration / 2, easing = LinearOutSlowInEasing),
                    ) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth / 4 },
                            animationSpec = tween(duration, easing = FastOutLinearInEasing),
                        ) + fadeOut(
                            animationSpec = tween(duration / 2, easing = FastOutLinearInEasing),
                        )
                },
                popTransitionSpec = {
                    val duration = 400
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth / 4 },
                        animationSpec = tween(duration, easing = LinearOutSlowInEasing),
                    ) + fadeIn(
                        animationSpec = tween(duration / 2, easing = LinearOutSlowInEasing),
                    ) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(duration, easing = FastOutLinearInEasing),
                        ) + fadeOut(
                            animationSpec = tween(duration / 2, easing = FastOutLinearInEasing),
                        )
                },
            )
        }
    }
}
