package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.model.Game
import kotlinx.coroutines.flow.Flow

/**
 * Repository for games data.
 */
interface GamesRepository {
    fun getGames(): Flow<List<Game>>
    fun getGamesByDate(date: String): Flow<List<Game>>
    fun getGameById(id: Int): Flow<Game?>
    fun getGamesByTeamId(teamId: Int): Flow<List<Game>>
    suspend fun syncGames()
    suspend fun syncGamesByDate(date: String)
}
