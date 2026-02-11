package com.hoopsnow.nba.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hoopsnow.nba.core.database.dao.GameDao
import com.hoopsnow.nba.core.database.dao.PlayerDao
import com.hoopsnow.nba.core.database.dao.TeamDao
import com.hoopsnow.nba.core.database.model.GameEntity
import com.hoopsnow.nba.core.database.model.PlayerEntity
import com.hoopsnow.nba.core.database.model.TeamEntity

@Database(
    entities = [
        GameEntity::class,
        PlayerEntity::class,
        TeamEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class NbaDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun playerDao(): PlayerDao
    abstract fun teamDao(): TeamDao
}
