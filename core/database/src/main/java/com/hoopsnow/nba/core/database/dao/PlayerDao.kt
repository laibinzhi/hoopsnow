package com.hoopsnow.nba.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hoopsnow.nba.core.database.model.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players ORDER BY id ASC")
    fun getPlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    fun getPlayerById(id: Int): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id IN (:ids)")
    fun getPlayersByIds(ids: Set<Int>): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE teamId = :teamId ORDER BY lastName, firstName")
    fun getPlayersByTeamId(teamId: Int): Flow<List<PlayerEntity>>

    @Query("""
        SELECT * FROM players
        WHERE firstName LIKE '%' || :query || '%'
           OR lastName LIKE '%' || :query || '%'
        ORDER BY id ASC
    """)
    fun searchPlayers(query: String): Flow<List<PlayerEntity>>

    @Upsert
    suspend fun upsertPlayers(players: List<PlayerEntity>)

    @Upsert
    suspend fun upsertPlayer(player: PlayerEntity)

    @Query("DELETE FROM players")
    suspend fun deleteAllPlayers()
}
