package com.hoopsnow.nba.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.hoopsnow.nba.ui.favorites.FavoritesListScreen
import com.hoopsnow.nba.ui.games.GamesListScreen
import com.hoopsnow.nba.ui.players.PlayersListScreen
import com.hoopsnow.nba.ui.teams.TeamsListScreen

object GamesTab : Tab {
    private fun readResolve(): Any = GamesTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.DateRange)
            return remember { TabOptions(index = 0u, title = "Games", icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(GamesListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object TeamsTab : Tab {
    private fun readResolve(): Any = TeamsTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.EmojiEvents)
            return remember { TabOptions(index = 1u, title = "Teams", icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(TeamsListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object PlayersTab : Tab {
    private fun readResolve(): Any = PlayersTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.People)
            return remember { TabOptions(index = 2u, title = "Players", icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(PlayersListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}

object FavoritesTab : Tab {
    private fun readResolve(): Any = FavoritesTab

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Outlined.StarBorder)
            return remember { TabOptions(index = 3u, title = "Favorites", icon = icon) }
        }

    @Composable
    override fun Content() {
        Navigator(FavoritesListScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
