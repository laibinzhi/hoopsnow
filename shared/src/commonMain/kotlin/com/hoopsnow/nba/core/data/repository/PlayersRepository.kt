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
    fun getPlayers(): Flow<List<Player>>
    fun getPlayerById(id: Int): Flow<Player?>
    fun getPlayersByIds(ids: Set<Int>): Flow<List<Player>>
    fun searchPlayers(query: String): Flow<List<Player>>
    fun getPlayersByTeamId(teamId: Int): Flow<List<Player>>
    suspend fun syncPlayers()
    suspend fun syncPlayersBySearch(query: String)
    suspend fun loadPlayersPage(
        cursor: Int? = null,
        perPage: Int = 25,
        search: String? = null,
    ): PlayersPageResult
    suspend fun syncPlayersByTeamId(teamId: Int)
}
