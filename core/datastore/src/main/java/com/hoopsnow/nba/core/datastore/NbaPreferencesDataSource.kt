package com.hoopsnow.nba.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NbaPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val favoritePlayerIds: Flow<Set<Int>> = dataStore.data.map { preferences ->
        preferences[FAVORITE_PLAYER_IDS]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    val favoriteTeamIds: Flow<Set<Int>> = dataStore.data.map { preferences ->
        preferences[FAVORITE_TEAM_IDS]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    suspend fun toggleFavoritePlayer(playerId: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_PLAYER_IDS] ?: emptySet()
            val playerIdString = playerId.toString()
            preferences[FAVORITE_PLAYER_IDS] = if (playerIdString in currentFavorites) {
                currentFavorites - playerIdString
            } else {
                currentFavorites + playerIdString
            }
        }
    }

    suspend fun toggleFavoriteTeam(teamId: Int) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_TEAM_IDS] ?: emptySet()
            val teamIdString = teamId.toString()
            preferences[FAVORITE_TEAM_IDS] = if (teamIdString in currentFavorites) {
                currentFavorites - teamIdString
            } else {
                currentFavorites + teamIdString
            }
        }
    }

    suspend fun setFavoritePlayer(playerId: Int, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_PLAYER_IDS] ?: emptySet()
            val playerIdString = playerId.toString()
            preferences[FAVORITE_PLAYER_IDS] = if (isFavorite) {
                currentFavorites + playerIdString
            } else {
                currentFavorites - playerIdString
            }
        }
    }

    suspend fun setFavoriteTeam(teamId: Int, isFavorite: Boolean) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_TEAM_IDS] ?: emptySet()
            val teamIdString = teamId.toString()
            preferences[FAVORITE_TEAM_IDS] = if (isFavorite) {
                currentFavorites + teamIdString
            } else {
                currentFavorites - teamIdString
            }
        }
    }

    private companion object {
        val FAVORITE_PLAYER_IDS = stringSetPreferencesKey("favorite_player_ids")
        val FAVORITE_TEAM_IDS = stringSetPreferencesKey("favorite_team_ids")
    }
}
