package com.hoopsnow.nba.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hoopsnow.nba.core.database.model.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY date DESC")
    fun getGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE date = :date ORDER BY id")
    fun getGamesByDate(date: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: Int): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE homeTeamId = :teamId OR visitorTeamId = :teamId ORDER BY date DESC")
    fun getGamesByTeamId(teamId: Int): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGames(games: List<GameEntity>)

    @Upsert
    suspend fun upsertGame(game: GameEntity)

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Query("DELETE FROM games WHERE id IN (:ids)")
    suspend fun deleteGames(ids: List<Int>)
}
