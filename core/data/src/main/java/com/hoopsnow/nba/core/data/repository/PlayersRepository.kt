package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.model.Player
import kotlinx.coroutines.flow.Flow

/**
 * Result of paginated player loading
 */
data class PlayersPageResult(
    val players: List<Player>,
    val nextCursor: Int?,
    val hasMore: Boolean,
)

/**
 * Repository for players data.
 */
interface PlayersRepository {
    /**
     * Get all players as a Flow
     */
    fun getPlayers(): Flow<List<Player>>

    /**
     * Get a specific player by ID
     */
    fun getPlayerById(id: Int): Flow<Player?>

    /**
     * Get players by IDs
     */
    fun getPlayersByIds(ids: Set<Int>): Flow<List<Player>>

    /**
     * Search players by name
     */
    fun searchPlayers(query: String): Flow<List<Player>>

    /**
     * Get players by team ID
     */
    fun getPlayersByTeamId(teamId: Int): Flow<List<Player>>

    /**
     * Sync players from network
     */
    suspend fun syncPlayers()

    /**
     * Sync players by search query from network
     */
    suspend fun syncPlayersBySearch(query: String)

    /**
     * Load players with pagination (cursor-based)
     * @param cursor The cursor for pagination (null for first page)
     * @param perPage Number of results per page
     * @param search Optional search query
     */
    suspend fun loadPlayersPage(
        cursor: Int? = null,
        perPage: Int = 25,
        search: String? = null,
    ): PlayersPageResult

    /**
     * Sync players by team ID from network
     */
    suspend fun syncPlayersByTeamId(teamId: Int)
}
