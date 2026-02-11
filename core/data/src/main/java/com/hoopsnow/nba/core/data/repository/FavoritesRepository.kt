package com.hoopsnow.nba.core.data.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for user favorites data.
 */
interface FavoritesRepository {
    /**
     * Flow of favorite team IDs
     */
    val favoriteTeamIds: Flow<Set<Int>>

    /**
     * Flow of favorite player IDs
     */
    val favoritePlayerIds: Flow<Set<Int>>

    /**
     * Toggle team favorite status
     */
    suspend fun toggleTeamFavorite(teamId: Int)

    /**
     * Toggle player favorite status
     */
    suspend fun togglePlayerFavorite(playerId: Int)

    /**
     * Set team favorite status
     */
    suspend fun setTeamFavorite(teamId: Int, isFavorite: Boolean)

    /**
     * Set player favorite status
     */
    suspend fun setPlayerFavorite(playerId: Int, isFavorite: Boolean)
}
