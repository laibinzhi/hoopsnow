package com.hoopsnow.nba.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hoopsnow.nba.core.database.NbaDatabase
import com.hoopsnow.nba.core.model.Player
import com.hoopsnow.nba.core.model.Team
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.asExternalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class OfflineFirstPlayersRepository(
    private val database: NbaDatabase,
    private val networkDataSource: NbaNetworkDataSource,
) : PlayersRepository {

    private val playerQueries get() = database.playerQueries
    private val teamQueries get() = database.teamQueries

    override fun getPlayers(): Flow<List<Player>> =
        playerQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toPlayer() } }
            .onStart { syncPlayers() }

    override fun getPlayerById(id: Int): Flow<Player?> =
        playerQueries.getById(id.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.firstOrNull()?.toPlayer() }

    override fun getPlayersByIds(ids: Set<Int>): Flow<List<Player>> =
        playerQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.filter { it.id.toInt() in ids }.map { it.toPlayer() }
            }

    override fun searchPlayers(query: String): Flow<List<Player>> =
        if (query.isBlank()) {
            getPlayers()
        } else {
            playerQueries.getAll()
                .asFlow()
                .mapToList(Dispatchers.Default)
                .map { entities ->
                    entities.filter {
                        it.firstName.contains(query, ignoreCase = true) ||
                            it.lastName.contains(query, ignoreCase = true)
                    }.map { it.toPlayer() }
                }
                .onStart { syncPlayersBySearch(query) }
        }

    override fun getPlayersByTeamId(teamId: Int): Flow<List<Player>> =
        playerQueries.getByTeamId(teamId.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toPlayer() } }
            .onStart { syncPlayersByTeamId(teamId) }

    override suspend fun syncPlayers() {
        try {
            val result = networkDataSource.getPlayers(perPage = 100)
            val players = result.data.map { it.asExternalModel() }
            upsertPlayers(players)
        } catch (_: Exception) {
        }
    }

    override suspend fun syncPlayersBySearch(query: String) {
        try {
            val result = networkDataSource.getPlayers(perPage = 100, search = query)
            val players = result.data.map { it.asExternalModel() }
            upsertPlayers(players)
        } catch (_: Exception) {
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
        upsertPlayers(players)
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
            upsertPlayers(players)
        } catch (_: Exception) {
        }
    }

    private fun upsertPlayers(players: List<Player>) {
        database.transaction {
            players.forEach { player ->
                playerQueries.upsert(
                    id = player.id.toLong(),
                    firstName = player.firstName,
                    lastName = player.lastName,
                    position = player.position,
                    teamId = player.team?.id?.toLong(),
                )
            }
        }
    }

    private fun com.hoopsnow.nba.core.database.PlayerEntity.toPlayer(): Player {
        val teamEntity = teamId?.let { tid ->
            teamQueries.getById(tid).executeAsOneOrNull()
        }
        return Player(
            id = id.toInt(),
            firstName = firstName,
            lastName = lastName,
            position = position,
            team = teamEntity?.let {
                Team(
                    id = it.id.toInt(),
                    conference = it.conference,
                    division = it.division,
                    city = it.city,
                    name = it.name,
                    fullName = it.fullName,
                    abbreviation = it.abbreviation,
                )
            },
        )
    }
}
