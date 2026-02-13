package com.hoopsnow.nba.core.network

import com.hoopsnow.nba.core.common.NbaException
import com.hoopsnow.nba.core.network.model.ApiResponse
import com.hoopsnow.nba.core.network.model.NetworkGame
import com.hoopsnow.nba.core.network.model.NetworkPlayer
import com.hoopsnow.nba.core.network.model.NetworkTeam
import com.hoopsnow.nba.core.network.model.PaginatedResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

private const val NBA_BASE_URL = "https://api.balldontlie.io/v1"
private const val API_KEY = "f85e8321-7a33-4611-92e2-6ef1a0e099dc"

/**
 * Ktor implementation of [NbaNetworkDataSource]
 */
class KtorNbaNetwork(
    private val httpClient: HttpClient,
) : NbaNetworkDataSource {

    override suspend fun getTeams(): List<NetworkTeam> {
        val response: ApiResponse<List<NetworkTeam>> = httpClient.get("$NBA_BASE_URL/teams") {
            header("Authorization", API_KEY)
        }.body()
        return response.data
    }

    override suspend fun getTeam(id: Int): NetworkTeam {
        val response: ApiResponse<NetworkTeam> = httpClient.get("$NBA_BASE_URL/teams/$id") {
            header("Authorization", API_KEY)
        }.body()
        return response.data
    }

    override suspend fun getPlayers(
        cursor: Int?,
        perPage: Int,
        search: String?,
        teamIds: List<Int>?,
    ): PaginatedResult<NetworkPlayer> {
        val httpResponse = httpClient.get("$NBA_BASE_URL/players") {
            header("Authorization", API_KEY)
            cursor?.let { parameter("cursor", it) }
            parameter("per_page", perPage)
            search?.let { parameter("search", it) }
            teamIds?.forEach { parameter("team_ids[]", it) }
        }

        if (httpResponse.status == HttpStatusCode.TooManyRequests) {
            throw NbaException.RateLimitException()
        }

        if (!httpResponse.status.isSuccess()) {
            throw NbaException.NetworkException("HTTP ${httpResponse.status.value}")
        }

        val response: ApiResponse<List<NetworkPlayer>> = httpResponse.body()
        return PaginatedResult(
            data = response.data,
            nextCursor = response.meta?.nextCursor,
        )
    }

    override suspend fun getPlayer(id: Int): NetworkPlayer {
        val response: ApiResponse<NetworkPlayer> = httpClient.get("$NBA_BASE_URL/players/$id") {
            header("Authorization", API_KEY)
        }.body()
        return response.data
    }

    override suspend fun getGames(
        page: Int,
        perPage: Int,
        dates: List<String>?,
        teamIds: List<Int>?,
    ): List<NetworkGame> {
        val response: ApiResponse<List<NetworkGame>> = httpClient.get("$NBA_BASE_URL/games") {
            header("Authorization", API_KEY)
            parameter("page", page)
            parameter("per_page", perPage)
            dates?.forEach { parameter("dates[]", it) }
            teamIds?.forEach { parameter("team_ids[]", it) }
        }.body()
        return response.data
    }

    override suspend fun getGame(id: Int): NetworkGame {
        val response: ApiResponse<NetworkGame> = httpClient.get("$NBA_BASE_URL/games/$id") {
            header("Authorization", API_KEY)
        }.body()
        return response.data
    }
}

private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299
