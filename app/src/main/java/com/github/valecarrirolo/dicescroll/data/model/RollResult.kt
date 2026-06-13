package com.github.valecarrirolo.dicescroll.data.model

import java.util.UUID

data class SingleDieRoll(
    val id: String = UUID.randomUUID().toString(),
    val diceType: DiceType,
    val value: Int
)

data class RollResult(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val rolls: List<SingleDieRoll>,
    val modifier: Int = 0
) {
    val total: Int get() = rolls.sumOf { it.value } + modifier
}
