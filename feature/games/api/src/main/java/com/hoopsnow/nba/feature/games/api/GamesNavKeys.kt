package com.hoopsnow.nba.feature.games.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object GamesNavKey : NavKey

@Serializable
data class GameDetailNavKey(val gameId: Int) : NavKey
