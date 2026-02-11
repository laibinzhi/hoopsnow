package com.hoopsnow.nba.core.testing.repository

import com.hoopsnow.nba.core.data.repository.GamesRepository
import com.hoopsnow.nba.core.model.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of [GamesRepository] for testing.
 */
class FakeGamesRepository : GamesRepository {

    private val _games = MutableStateFlow<List<Game>>(emptyList())

    override fun getGames(): Flow<List<Game>> = _games

    override fun getGamesByDate(date: String): Flow<List<Game>> = _games.map { games ->
        games.filter { it.date == date }
    }

    override fun getGameById(id: Int): Flow<Game?> = _games.map { games ->
        games.find { it.id == id }
    }

    override fun getGamesByTeamId(teamId: Int): Flow<List<Game>> = _games.map { games ->
        games.filter { it.homeTeam.id == teamId || it.visitorTeam.id == teamId }
    }

    override suspend fun syncGames() {
        // No-op for fake
    }

    override suspend fun syncGamesByDate(date: String) {
        // No-op for fake
    }

    /**
     * Set games for testing.
     */
    fun setGames(games: List<Game>) {
        _games.value = games
    }

    /**
     * Add a game for testing.
     */
    fun addGame(game: Game) {
        _games.value = _games.value + game
    }
}
