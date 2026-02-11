package com.hoopsnow.nba.core.testing.repository

import com.hoopsnow.nba.core.data.repository.PlayersPageResult
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of [PlayersRepository] for testing.
 */
class FakePlayersRepository : PlayersRepository {

    private val _players = MutableStateFlow<List<Player>>(emptyList())

    override fun getPlayers(): Flow<List<Player>> = _players

    override fun getPlayerById(id: Int): Flow<Player?> = _players.map { players ->
        players.find { it.id == id }
    }

    override fun getPlayersByIds(ids: Set<Int>): Flow<List<Player>> = _players.map { players ->
        players.filter { it.id in ids }
    }

    override fun searchPlayers(query: String): Flow<List<Player>> = _players.map { players ->
        players.filter {
            it.firstName.contains(query, ignoreCase = true) ||
                it.lastName.contains(query, ignoreCase = true)
        }
    }

    override fun getPlayersByTeamId(teamId: Int): Flow<List<Player>> = _players.map { players ->
        players.filter { it.team?.id == teamId }
    }

    override suspend fun syncPlayers() {
        // No-op for fake
    }

    override suspend fun syncPlayersBySearch(query: String) {
        // No-op for fake
    }

    override suspend fun loadPlayersPage(
        cursor: Int?,
        perPage: Int,
        search: String?,
    ): PlayersPageResult {
        val filtered = if (search.isNullOrBlank()) {
            _players.value
        } else {
            _players.value.filter {
                it.firstName.contains(search, ignoreCase = true) ||
                    it.lastName.contains(search, ignoreCase = true)
            }
        }
        return PlayersPageResult(
            players = filtered.take(perPage),
            nextCursor = null,
            hasMore = false,
        )
    }

    override suspend fun syncPlayersByTeamId(teamId: Int) {
        // No-op for fake
    }

    /**
     * Set players for testing.
     */
    fun setPlayers(players: List<Player>) {
        _players.value = players
    }

    /**
     * Add a player for testing.
     */
    fun addPlayer(player: Player) {
        _players.value = _players.value + player
    }
}
