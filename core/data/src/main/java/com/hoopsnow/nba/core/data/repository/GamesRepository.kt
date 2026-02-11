package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Repository for games data.
 */
interface GamesRepository {
    /**
     * Get all games as a Flow
     */
    fun getGames(): Flow<List<Game>>

    /**
     * Get games by date (format: "YYYY-MM-DD")
     */
    fun getGamesByDate(date: String): Flow<List<Game>>

    /**
     * Get a specific game by ID
     */
    fun getGameById(id: Int): Flow<Game?>

    /**
     * Get games by team ID
     */
    fun getGamesByTeamId(teamId: Int): Flow<List<Game>>

    /**
     * Sync games from network
     */
    suspend fun syncGames()

    /**
     * Sync games by date from network
     */
    suspend fun syncGamesByDate(date: String)
}
