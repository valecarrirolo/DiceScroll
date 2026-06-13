package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tray_dice")
data class TrayDieEntity(
  @PrimaryKey @ColumnInfo(name = "dice_type_name") val diceTypeName: String,
  @ColumnInfo(name = "count") val count: Int,
)
