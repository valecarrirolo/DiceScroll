package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "single_die_rolls",
    foreignKeys = [
        ForeignKey(
            entity = RollResultEntity::class,
            parentColumns = ["id"],
            childColumns = ["roll_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roll_id")]
)
data class SingleDieRollEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "roll_id") val rollId: String,
    @ColumnInfo(name = "sort_index") val sortIndex: Int,
    @ColumnInfo(name = "dice_type_name") val diceTypeName: String,
    @ColumnInfo(name = "dice_display_name") val diceDisplayName: String,
    @ColumnInfo(name = "dice_faces") val diceFaces: Int,
    @ColumnInfo(name = "dice_color_hex") val diceColorHex: String,
    @ColumnInfo(name = "value") val value: Int
)
