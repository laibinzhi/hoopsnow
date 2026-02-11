package com.hoopsnow.nba.feature.teams.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object TeamsNavKey : NavKey

@Serializable
data class TeamDetailNavKey(val teamId: Int) : NavKey
