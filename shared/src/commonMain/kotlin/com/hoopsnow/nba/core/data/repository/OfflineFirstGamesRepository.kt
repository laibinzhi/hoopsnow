package com.hoopsnow.nba.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hoopsnow.nba.core.database.NbaDatabase
import com.hoopsnow.nba.core.model.Game
import com.hoopsnow.nba.core.model.Team
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.asExternalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class OfflineFirstGamesRepository(
    private val database: NbaDatabase,
    private val networkDataSource: NbaNetworkDataSource,
) : GamesRepository {

    private val gameQueries get() = database.gameQueries
    private val teamQueries get() = database.teamQueries

    override fun getGames(): Flow<List<Game>> =
        gameQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toGame() } }
            .onStart { syncGames() }

    override fun getGamesByDate(date: String): Flow<List<Game>> =
        gameQueries.getByDate(date)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toGame() } }
            .onStart { syncGamesByDate(date) }

    override fun getGameById(id: Int): Flow<Game?> =
        gameQueries.getById(id.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.firstOrNull()?.toGame() }

    override fun getGamesByTeamId(teamId: Int): Flow<List<Game>> =
        gameQueries.getByTeamId(teamId.toLong(), teamId.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toGame() } }

    override suspend fun syncGames() {
        try {
            val networkGames = networkDataSource.getGames(perPage = 100)
            val games = networkGames.map { it.asExternalModel() }
            upsertGames(games)
        } catch (_: Exception) {
        }
    }

    override suspend fun syncGamesByDate(date: String) {
        try {
            val networkGames = networkDataSource.getGames(perPage = 100, dates = listOf(date))
            val games = networkGames.map { it.asExternalModel() }
            upsertGames(games)
        } catch (_: Exception) {
        }
    }

    private fun upsertGames(games: List<Game>) {
        database.transaction {
            games.forEach { game ->
                gameQueries.upsert(
                    id = game.id.toLong(),
                    date = game.date,
                    season = game.season.toLong(),
                    homeTeamScore = game.homeTeamScore.toLong(),
                    visitorTeamScore = game.visitorTeamScore.toLong(),
                    homeTeamId = game.homeTeam.id.toLong(),
                    visitorTeamId = game.visitorTeam.id.toLong(),
                    status = game.status,
                )
            }
        }
    }

    private fun com.hoopsnow.nba.core.database.GameEntity.toGame(): Game {
        val homeTeamEntity = teamQueries.getById(homeTeamId).executeAsOneOrNull()
        val visitorTeamEntity = teamQueries.getById(visitorTeamId).executeAsOneOrNull()

        val unknownTeam = Team(
            id = 0, conference = "", division = "", city = "",
            name = "Unknown", fullName = "Unknown Team", abbreviation = "UNK",
        )

        return Game(
            id = id.toInt(),
            date = date,
            season = season.toInt(),
            homeTeamScore = homeTeamScore.toInt(),
            visitorTeamScore = visitorTeamScore.toInt(),
            homeTeam = homeTeamEntity?.let {
                Team(it.id.toInt(), it.conference, it.division, it.city, it.name, it.fullName, it.abbreviation)
            } ?: unknownTeam,
            visitorTeam = visitorTeamEntity?.let {
                Team(it.id.toInt(), it.conference, it.division, it.city, it.name, it.fullName, it.abbreviation)
            } ?: unknownTeam,
            status = status,
        )
    }
}
