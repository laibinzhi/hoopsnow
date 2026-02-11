package com.hoopsnow.nba.core.network.retrofit

import com.hoopsnow.nba.core.common.result.NbaException
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.core.network.model.ApiResponse
import com.hoopsnow.nba.core.network.model.NetworkGame
import com.hoopsnow.nba.core.network.model.NetworkPlayer
import com.hoopsnow.nba.core.network.model.NetworkTeam
import com.hoopsnow.nba.core.network.model.PaginatedResult
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private const val NBA_BASE_URL = "https://api.balldontlie.io/v1/"
private const val API_KEY = "f85e8321-7a33-4611-92e2-6ef1a0e099dc"

/**
 * Interceptor to add API key to all requests
 */
class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", API_KEY)
            .build()
        return chain.proceed(request)
    }
}

/**
 * Retrofit API interface for NBA endpoints
 */
private interface RetrofitNbaApi {

    @GET("teams")
    suspend fun getTeams(): ApiResponse<List<NetworkTeam>>

    @GET("teams/{id}")
    suspend fun getTeam(@Path("id") id: Int): ApiResponse<NetworkTeam>

    @GET("players")
    suspend fun getPlayers(
        @Query("cursor") cursor: Int?,
        @Query("per_page") perPage: Int,
        @Query("search") search: String?,
        @Query("team_ids[]") teamIds: List<Int>?,
    ): retrofit2.Response<ApiResponse<List<NetworkPlayer>>>

    @GET("players/{id}")
    suspend fun getPlayer(@Path("id") id: Int): ApiResponse<NetworkPlayer>

    @GET("games")
    suspend fun getGames(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("dates[]") dates: List<String>?,
        @Query("team_ids[]") teamIds: List<Int>?,
    ): ApiResponse<List<NetworkGame>>

    @GET("games/{id}")
    suspend fun getGame(@Path("id") id: Int): ApiResponse<NetworkGame>
}

/**
 * Retrofit implementation of [NbaNetworkDataSource]
 */
@Singleton
internal class RetrofitNbaNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : NbaNetworkDataSource {

    private val networkApi = Retrofit.Builder()
        .baseUrl(NBA_BASE_URL)
        .callFactory { okhttpCallFactory.get().newCall(it) }
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(RetrofitNbaApi::class.java)

    override suspend fun getTeams(): List<NetworkTeam> =
        networkApi.getTeams().data

    override suspend fun getTeam(id: Int): NetworkTeam =
        networkApi.getTeam(id).data

    override suspend fun getPlayers(
        cursor: Int?,
        perPage: Int,
        search: String?,
        teamIds: List<Int>?,
    ): PaginatedResult<NetworkPlayer> {
        val response = networkApi.getPlayers(cursor, perPage, search, teamIds)
        if (response.code() == 429) {
            throw NbaException.RateLimitException()
        }
        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}: ${response.message()}")
        }
        val body = response.body() ?: throw Exception("Empty response body")
        return PaginatedResult(
            data = body.data,
            nextCursor = body.meta?.nextCursor,
        )
    }

    override suspend fun getPlayer(id: Int): NetworkPlayer =
        networkApi.getPlayer(id).data

    override suspend fun getGames(
        page: Int,
        perPage: Int,
        dates: List<String>?,
        teamIds: List<Int>?,
    ): List<NetworkGame> =
        networkApi.getGames(page, perPage, dates, teamIds).data

    override suspend fun getGame(id: Int): NetworkGame =
        networkApi.getGame(id).data
}
