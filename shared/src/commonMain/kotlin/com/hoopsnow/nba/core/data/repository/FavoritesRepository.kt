package com.hoopsnow.nba.core.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for user favorites data.
 */
interface FavoritesRepository {
    val favoriteTeamIds: Flow<Set<Int>>
    val favoritePlayerIds: Flow<Set<Int>>
    suspend fun toggleTeamFavorite(teamId: Int)
    suspend fun togglePlayerFavorite(playerId: Int)
    suspend fun setTeamFavorite(teamId: Int, isFavorite: Boolean)
    suspend fun setPlayerFavorite(playerId: Int, isFavorite: Boolean)
}
