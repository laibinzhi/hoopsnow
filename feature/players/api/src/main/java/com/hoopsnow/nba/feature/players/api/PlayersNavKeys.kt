package com.hoopsnow.nba.feature.players.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object PlayersNavKey : NavKey

@Serializable
object PlayerSearchNavKey : NavKey

@Serializable
data class PlayerDetailNavKey(val playerId: Int) : NavKey
