package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roll_results")
data class RollResultEntity(
  @PrimaryKey val id: String,
  @ColumnInfo(name = "timestamp") val timestamp: Long,
  @ColumnInfo(name = "modifier") val modifier: Int,
)
