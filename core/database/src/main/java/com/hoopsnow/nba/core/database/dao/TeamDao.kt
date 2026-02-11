package com.hoopsnow.nba.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hoopsnow.nba.core.database.model.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams ORDER BY fullName")
    fun getTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE id = :id")
    fun getTeamById(id: Int): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE id IN (:ids)")
    fun getTeamsByIds(ids: Set<Int>): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE conference = :conference ORDER BY fullName")
    fun getTeamsByConference(conference: String): Flow<List<TeamEntity>>

    @Upsert
    suspend fun upsertTeams(teams: List<TeamEntity>)

    @Upsert
    suspend fun upsertTeam(team: TeamEntity)

    @Query("DELETE FROM teams")
    suspend fun deleteAllTeams()
}
