package com.hoopsnow.nba.core.data.di

import android.content.Context
import com.hoopsnow.nba.core.data.PlayerHeadshotProvider
import com.hoopsnow.nba.core.data.PlayerHeadshotProviderImpl
import com.hoopsnow.nba.core.data.TeamLogoProvider
import com.hoopsnow.nba.core.data.TeamLogoProviderImpl
import com.hoopsnow.nba.core.data.repository.FavoritesRepository
import com.hoopsnow.nba.core.data.repository.GamesRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstFavoritesRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstGamesRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstPlayersRepository
import com.hoopsnow.nba.core.data.repository.OfflineFirstTeamsRepository
import com.hoopsnow.nba.core.data.repository.PlayersRepository
import com.hoopsnow.nba.core.data.repository.TeamsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsFavoritesRepository(
        impl: OfflineFirstFavoritesRepository,
    ): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindsGamesRepository(
        impl: OfflineFirstGamesRepository,
    ): GamesRepository

    @Binds
    @Singleton
    abstract fun bindsPlayersRepository(
        impl: OfflineFirstPlayersRepository,
    ): PlayersRepository

    @Binds
    @Singleton
    abstract fun bindsTeamsRepository(
        impl: OfflineFirstTeamsRepository,
    ): TeamsRepository

    companion object {
        @Provides
        @Singleton
        fun providesTeamLogoProvider(
            @ApplicationContext context: Context,
        ): TeamLogoProvider = TeamLogoProviderImpl(context)

        @Provides
        @Singleton
        fun providesPlayerHeadshotProvider(
            @ApplicationContext context: Context,
        ): PlayerHeadshotProvider = PlayerHeadshotProviderImpl(context)
    }
}
