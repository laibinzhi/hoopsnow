package com.hoopsnow.nba.core.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hoopsnow.nba.core.database.NbaDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFirstFavoritesRepository(
    private val database: NbaDatabase,
) : FavoritesRepository {

    private val favoritePlayerQueries get() = database.favoritePlayerQueries
    private val favoriteTeamQueries get() = database.favoriteTeamQueries

    override val favoriteTeamIds: Flow<Set<Int>> =
        favoriteTeamQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { ids -> ids.map { it.toInt() }.toSet() }

    override val favoritePlayerIds: Flow<Set<Int>> =
        favoritePlayerQueries.getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { ids -> ids.map { it.toInt() }.toSet() }

    override suspend fun toggleTeamFavorite(teamId: Int) {
        val exists = favoriteTeamQueries.exists(teamId.toLong()).executeAsOne() > 0
        if (exists) {
            favoriteTeamQueries.delete(teamId.toLong())
        } else {
            favoriteTeamQueries.insert(teamId.toLong())
        }
    }

    override suspend fun togglePlayerFavorite(playerId: Int) {
        val exists = favoritePlayerQueries.exists(playerId.toLong()).executeAsOne() > 0
        if (exists) {
            favoritePlayerQueries.delete(playerId.toLong())
        } else {
            favoritePlayerQueries.insert(playerId.toLong())
        }
    }

    override suspend fun setTeamFavorite(teamId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteTeamQueries.insert(teamId.toLong())
        } else {
            favoriteTeamQueries.delete(teamId.toLong())
        }
    }

    override suspend fun setPlayerFavorite(playerId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            favoritePlayerQueries.insert(playerId.toLong())
        } else {
            favoritePlayerQueries.delete(playerId.toLong())
        }
    }
}
