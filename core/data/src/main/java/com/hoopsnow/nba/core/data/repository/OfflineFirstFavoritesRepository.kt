package com.hoopsnow.nba.core.data.repository

import com.hoopsnow.nba.core.datastore.NbaPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstFavoritesRepository @Inject constructor(
    private val preferencesDataSource: NbaPreferencesDataSource,
) : FavoritesRepository {

    override val favoriteTeamIds: Flow<Set<Int>> = preferencesDataSource.favoriteTeamIds

    override val favoritePlayerIds: Flow<Set<Int>> = preferencesDataSource.favoritePlayerIds

    override suspend fun toggleTeamFavorite(teamId: Int) {
        preferencesDataSource.toggleFavoriteTeam(teamId)
    }

    override suspend fun togglePlayerFavorite(playerId: Int) {
        preferencesDataSource.toggleFavoritePlayer(playerId)
    }

    override suspend fun setTeamFavorite(teamId: Int, isFavorite: Boolean) {
        preferencesDataSource.setFavoriteTeam(teamId, isFavorite)
    }

    override suspend fun setPlayerFavorite(playerId: Int, isFavorite: Boolean) {
        preferencesDataSource.setFavoritePlayer(playerId, isFavorite)
    }
}
