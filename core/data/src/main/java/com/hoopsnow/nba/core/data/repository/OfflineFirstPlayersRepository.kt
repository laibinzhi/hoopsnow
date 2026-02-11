package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.database.dao.PlayerDao
import com.hoopsnow.nba.core.database.model.asEntity
import com.hoopsnow.nba.core.database.model.asExternalModel
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class OfflineFirstPlayersRepository @Inject constructor(
    private val playerDao: PlayerDao,
    private val networkDataSource: NbaNetworkDataSource,
) : PlayersRepository {

    override fun getPlayers(): Flow<List<Player>> =
        playerDao.getPlayers()
            .map { entities -> entities.map { it.asExternalModel() } }
            .onStart { syncPlayers() }

    override fun getPlayerById(id: Int): Flow<Player?> =
        playerDao.getPlayerById(id)
            .map { it?.asExternalModel() }

    override fun getPlayersByIds(ids: Set<Int>): Flow<List<Player>> =
        playerDao.getPlayersByIds(ids)
            .map { entities -> entities.map { it.asExternalModel() } }

    override fun searchPlayers(query: String): Flow<List<Player>> =
        if (query.isBlank()) {
            getPlayers()
        } else {
            playerDao.searchPlayers(query)
                .map { entities -> entities.map { it.asExternalModel() } }
                .onStart { syncPlayersBySearch(query) }
        }

    override fun getPlayersByTeamId(teamId: Int): Flow<List<Player>> =
        playerDao.getPlayersByTeamId(teamId)
            .map { entities -> entities.map { it.asExternalModel() } }
            .onStart { syncPlayersByTeamId(teamId) }

    override suspend fun syncPlayers() {
        try {
            val result = networkDataSource.getPlayers(perPage = 100)
            val players = result.data.map { it.asExternalModel() }
            playerDao.upsertPlayers(players.map { it.asEntity() })
        } catch (e: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }

    override suspend fun syncPlayersBySearch(query: String) {
        try {
            val result = networkDataSource.getPlayers(perPage = 100, search = query)
            val players = result.data.map { it.asExternalModel() }
            playerDao.upsertPlayers(players.map { it.asEntity() })
        } catch (e: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }

    override suspend fun loadPlayersPage(
        cursor: Int?,
        perPage: Int,
        search: String?,
    ): PlayersPageResult {
        val result = networkDataSource.getPlayers(
            cursor = cursor,
            perPage = perPage,
            search = search,
        )
        val players = result.data.map { it.asExternalModel() }
        // Cache to local database
        playerDao.upsertPlayers(players.map { it.asEntity() })
        return PlayersPageResult(
            players = players,
            nextCursor = result.nextCursor,
            hasMore = result.hasMore,
        )
    }

    override suspend fun syncPlayersByTeamId(teamId: Int) {
        try {
            val result = networkDataSource.getPlayers(perPage = 100, teamIds = listOf(teamId))
            val players = result.data.map { it.asExternalModel() }
            playerDao.upsertPlayers(players.map { it.asEntity() })
        } catch (e: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }
}
