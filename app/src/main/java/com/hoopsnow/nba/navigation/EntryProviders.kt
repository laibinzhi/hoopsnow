package com.hoopsnow.nba.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.hoopsnow.nba.feature.favorites.api.FavoritesNavKey
import com.hoopsnow.nba.feature.favorites.impl.FavoritesListScreen
import com.hoopsnow.nba.feature.games.api.GameDetailNavKey
import com.hoopsnow.nba.feature.games.api.GamesNavKey
import com.hoopsnow.nba.feature.games.impl.GameDetailScreen
import com.hoopsnow.nba.feature.games.impl.GamesListScreen
import com.hoopsnow.nba.feature.players.api.PlayerDetailNavKey
import com.hoopsnow.nba.feature.players.api.PlayerSearchNavKey
import com.hoopsnow.nba.feature.players.api.PlayersNavKey
import com.hoopsnow.nba.feature.players.impl.PlayerDetailScreen
import com.hoopsnow.nba.feature.players.impl.PlayerSearchScreen
import com.hoopsnow.nba.feature.players.impl.PlayersListScreen
import com.hoopsnow.nba.feature.teams.api.TeamDetailNavKey
import com.hoopsnow.nba.feature.teams.api.TeamsNavKey
import com.hoopsnow.nba.feature.teams.impl.TeamDetailScreen
import com.hoopsnow.nba.feature.teams.impl.TeamsListScreen

fun EntryProviderScope<NavKey>.gamesEntry(navigator: Navigator) {
    entry<GamesNavKey> {
        GamesListScreen(
            onGameClick = navigator::navigateToGameDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.gameDetailEntry(navigator: Navigator) {
    entry<GameDetailNavKey> { key ->
        GameDetailScreen(
            gameId = key.gameId,
            onBack = navigator::goBack,
            onTeamClick = navigator::navigateToTeamDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.teamsEntry(navigator: Navigator) {
    entry<TeamsNavKey> {
        TeamsListScreen(
            onTeamClick = navigator::navigateToTeamDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.teamDetailEntry(navigator: Navigator) {
    entry<TeamDetailNavKey> { key ->
        TeamDetailScreen(
            teamId = key.teamId,
            onBack = navigator::goBack,
            onGameClick = navigator::navigateToGameDetail,
            onPlayerClick = navigator::navigateToPlayerDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.playersEntry(navigator: Navigator) {
    entry<PlayersNavKey> {
        PlayersListScreen(
            onPlayerClick = navigator::navigateToPlayerDetail,
            onSearchClick = navigator::navigateToPlayerSearch,
        )
    }
}

fun EntryProviderScope<NavKey>.playerSearchEntry(navigator: Navigator) {
    entry<PlayerSearchNavKey> {
        PlayerSearchScreen(
            onBack = navigator::goBack,
            onPlayerClick = navigator::navigateToPlayerDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.playerDetailEntry(navigator: Navigator) {
    entry<PlayerDetailNavKey> { key ->
        PlayerDetailScreen(
            playerId = key.playerId,
            onBack = navigator::goBack,
            onTeamClick = navigator::navigateToTeamDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.favoritesEntry(navigator: Navigator) {
    entry<FavoritesNavKey> {
        FavoritesListScreen(
            onPlayerClick = navigator::navigateToPlayerDetail,
            onTeamClick = navigator::navigateToTeamDetail,
        )
    }
}
