package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AppStateEntity::class,
        RollResultEntity::class,
        SingleDieRollEntity::class,
        TrayDieEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DiceScrollDatabase : RoomDatabase() {
    abstract fun diceScrollDao(): DiceScrollDao
}
