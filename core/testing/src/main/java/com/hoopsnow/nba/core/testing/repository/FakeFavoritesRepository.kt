package com.hoopsnow.nba.core.testing.repository

import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake implementation of [FavoritesRepository] for testing.
 */
class FakeFavoritesRepository : FavoritesRepository {

    private val _favoriteTeamIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteTeamIds: Flow<Set<Int>> = _favoriteTeamIds.asStateFlow()

    private val _favoritePlayerIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoritePlayerIds: Flow<Set<Int>> = _favoritePlayerIds.asStateFlow()

    override suspend fun toggleTeamFavorite(teamId: Int) {
        val current = _favoriteTeamIds.value
        _favoriteTeamIds.value = if (teamId in current) {
            current - teamId
        } else {
            current + teamId
        }
    }

    override suspend fun togglePlayerFavorite(playerId: Int) {
        val current = _favoritePlayerIds.value
        _favoritePlayerIds.value = if (playerId in current) {
            current - playerId
        } else {
            current + playerId
        }
    }

    override suspend fun setTeamFavorite(teamId: Int, isFavorite: Boolean) {
        val current = _favoriteTeamIds.value
        _favoriteTeamIds.value = if (isFavorite) {
            current + teamId
        } else {
            current - teamId
        }
    }

    override suspend fun setPlayerFavorite(playerId: Int, isFavorite: Boolean) {
        val current = _favoritePlayerIds.value
        _favoritePlayerIds.value = if (isFavorite) {
            current + playerId
        } else {
            current - playerId
        }
    }

    /**
     * Set initial favorite team IDs for testing.
     */
    fun setFavoriteTeamIds(ids: Set<Int>) {
        _favoriteTeamIds.value = ids
    }

    /**
     * Set initial favorite player IDs for testing.
     */
    fun setFavoritePlayerIds(ids: Set<Int>) {
        _favoritePlayerIds.value = ids
    }
}
