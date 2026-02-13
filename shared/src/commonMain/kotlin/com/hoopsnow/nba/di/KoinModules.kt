package com.hoopsnow.nba.di

import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.GamesRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstFavoritesRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstGamesRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstPlayersRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstTeamsRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.data.repository.TeamsRepository
import com.hoopsnow.nba.core.database.DatabaseDriverFactory
import com.hoopsnow.nba.core.database.NbaDatabase
import com.hoopsnow.nba.core.network.KtorNbaNetwork
import com.hoopsnow.nba.core.network.NbaNetworkDataSource
import com.hoopsnow.nba.ui.favorites.FavoritesListScreenModel
import com.hoopsnow.nba.ui.games.GameDetailScreenModel
import com.hoopsnow.nba.ui.games.GamesListScreenModel
import com.hoopsnow.nba.ui.players.PlayerDetailScreenModel
import com.hoopsnow.nba.ui.players.PlayerSearchScreenModel
import com.hoopsnow.nba.ui.players.PlayersListScreenModel
import com.hoopsnow.nba.ui.teams.TeamDetailScreenModel
import com.hoopsnow.nba.ui.teams.TeamsListScreenModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sharedModule = module {
    // JSON
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    // HttpClient
    single {
        HttpClient(get()) {
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                level = LogLevel.BODY
            }
        }
    }

    // Database
    single {
        val driverFactory: DatabaseDriverFactory = get()
        NbaDatabase(driverFactory.createDriver())
    }

    // Network
    single<NbaNetworkDataSource> { KtorNbaNetwork(get()) }

    // Repositories
    single<TeamsRepository> { OfflineFirstTeamsRepository(get(), get()) }
    single<PlayersRepository> { OfflineFirstPlayersRepository(get(), get()) }
    single<GamesRepository> { OfflineFirstGamesRepository(get(), get()) }
    single<FavoritesRepository> { OfflineFirstFavoritesRepository(get()) }

    // ScreenModels
    factoryOf(::GamesListScreenModel)
    factoryOf(::GameDetailScreenModel)
    factoryOf(::TeamsListScreenModel)
    factoryOf(::TeamDetailScreenModel)
    factoryOf(::PlayersListScreenModel)
    factoryOf(::PlayerDetailScreenModel)
    factoryOf(::PlayerSearchScreenModel)
    factoryOf(::FavoritesListScreenModel)
}

expect fun platformModule(): Module
