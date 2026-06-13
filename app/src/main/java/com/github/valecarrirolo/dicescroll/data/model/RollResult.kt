package com.github.valecarrirolo.dicescroll.data.model

import java.util.UUID

data class DiceSnapshot(
  val typeId: String,
  val displayName: String,
  val faces: Int,
  val colorHex: String,
)

fun DiceType.toSnapshot(): DiceSnapshot =
  DiceSnapshot(typeId = name, displayName = displayName, faces = maxVal, colorHex = colorHex)

data class SingleDieRoll(
  val id: String = UUID.randomUUID().toString(),
  val diceType: DiceType,
  val value: Int,
  val diceSnapshot: DiceSnapshot = diceType.toSnapshot(),
)

data class RollSnapshot(val dice: List<DiceSnapshot>, val values: List<Int>, val modifier: Int)

data class RollResult(
  val id: String = UUID.randomUUID().toString(),
  val timestamp: Long = System.currentTimeMillis(),
  val rolls: List<SingleDieRoll>,
  val modifier: Int = 0,
) {
  val total: Int
    get() = rolls.sumOf { it.value } + modifier

  val snapshot: RollSnapshot
    get() =
      RollSnapshot(
        dice = rolls.map { it.diceSnapshot },
        values = rolls.map { it.value },
        modifier = modifier,
      )
}
