package com.hoopsnow.nba.core.database.di

import android.content.Context
import androidx.room.Room
import com.hoopsnow.nba.core.database.NbaDatabase
import com.hoopsnow.nba.core.database.dao.GameDao
import com.hoopsnow.nba.core.database.dao.PlayerDao
import com.hoopsnow.nba.core.database.dao.TeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesNbaDatabase(
        @ApplicationContext context: Context,
    ): NbaDatabase = Room.databaseBuilder(
        context,
        NbaDatabase::class.java,
        "nba-database",
    ).build()

    @Provides
    fun providesGameDao(database: NbaDatabase): GameDao = database.gameDao()

    @Provides
    fun providesPlayerDao(database: NbaDatabase): PlayerDao = database.playerDao()

    @Provides
    fun providesTeamDao(database: NbaDatabase): TeamDao = database.teamDao()
}
