package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_state")
data class AppStateEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "modifier") val modifier: Int
)
