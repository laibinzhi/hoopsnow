package com.hoopsnow.nba.core.network

import com.hoopsnow.nba.core.network.model.NetworkGame
import com.hoopsnow.nba.core.network.model.NetworkPlayer
import com.hoopsnow.nba.core.network.model.NetworkTeam
import com.hoopsnow.nba.core.network.model.PaginatedResult

/**
 * Interface representing network calls to the NBA API
 */
interface NbaNetworkDataSource {

    /**
     * Get all teams
     */
    suspend fun getTeams(): List<NetworkTeam>

    /**
     * Get a specific team by ID
     */
    suspend fun getTeam(id: Int): NetworkTeam

    /**
     * Get players with cursor-based pagination
     * @param cursor The cursor for pagination (null for first page)
     * @param perPage Number of results per page (max 100)
     * @param search Optional search query for player name
     * @param teamIds Optional list of team IDs to filter by
     */
    suspend fun getPlayers(
        cursor: Int? = null,
        perPage: Int = 25,
        search: String? = null,
        teamIds: List<Int>? = null,
    ): PaginatedResult<NetworkPlayer>

    /**
     * Get a specific player by ID
     */
    suspend fun getPlayer(id: Int): NetworkPlayer

    /**
     * Get games with optional date filter
     * @param dates List of dates in format "YYYY-MM-DD"
     */
    suspend fun getGames(
        page: Int = 0,
        perPage: Int = 50,
        dates: List<String>? = null,
        teamIds: List<Int>? = null,
    ): List<NetworkGame>

    /**
     * Get a specific game by ID
     */
    suspend fun getGame(id: Int): NetworkGame
}
