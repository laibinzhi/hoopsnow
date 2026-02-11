package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.database.dao.GameDao
import com.hoopsnow.nba.core.database.model.asEntity
import com.hoopsnow.nba.core.database.model.asExternalModel
import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.asExternalModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

internal class OfflineFirstGamesRepository @Inject constructor(
    private val gameDao: GameDao,
    private val networkDataSource: NbaNetworkDataSource,
) : GamesRepository {

    override fun getGames(): Flow<List<Game>> =
        gameDao.getGames()
            .map { entities -> entities.map { it.asExternalModel() } }
            .onStart { syncGames() }

    override fun getGamesByDate(date: String): Flow<List<Game>> =
        gameDao.getGamesByDate(date)
            .map { entities -> entities.map { it.asExternalModel() } }
            .onStart { syncGamesByDate(date) }

    override fun getGameById(id: Int): Flow<Game?> =
        gameDao.getGameById(id)
            .map { it?.asExternalModel() }

    override fun getGamesByTeamId(teamId: Int): Flow<List<Game>> =
        gameDao.getGamesByTeamId(teamId)
            .map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun syncGames() {
        try {
            val networkGames = networkDataSource.getGames(perPage = 100)
            val games = networkGames.map { it.asExternalModel() }
            gameDao.upsertGames(games.map { it.asEntity() })
        } catch (e: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }

    override suspend fun syncGamesByDate(date: String) {
        try {
            val networkGames = networkDataSource.getGames(perPage = 100, dates = listOf(date))
            val games = networkGames.map { it.asExternalModel() }
            gameDao.upsertGames(games.map { it.asEntity() })
        } catch (e: Exception) {
            // Silently fail - offline first means we show cached data
        }
    }
}
