package com.github.valecarrirolo.dicescroll.ui.screens.history

import com.github.valecarrirolo.dicescroll.data.model.RollResult

internal data class HistoryStats(
  val rollCount: Int,
  val diceRolled: Int,
  val averageTotal: Double,
  val minTotal: Int,
  val maxTotal: Int,
)

internal fun List<RollResult>.toHistoryStats(): HistoryStats? {
  if (isEmpty()) return null

  val totals = map { roll -> roll.total }
  return HistoryStats(
    rollCount = size,
    diceRolled = sumOf { roll -> roll.rolls.size },
    averageTotal = totals.average(),
    minTotal = totals.min(),
    maxTotal = totals.max(),
  )
}
