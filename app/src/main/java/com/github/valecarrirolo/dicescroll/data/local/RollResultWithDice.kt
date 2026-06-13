package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RollResultWithDice(
    @Embedded val roll: RollResultEntity,
    @Relation(parentColumn = "id", entityColumn = "roll_id")
    val diceRolls: List<SingleDieRollEntity>
)
