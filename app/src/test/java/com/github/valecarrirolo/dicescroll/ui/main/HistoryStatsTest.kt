package com.github.valecarrirolo.dicescroll.ui.main

import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class HistoryStatsTest {

  @Test
  fun toHistoryStats_emptyHistory_returnsNull() {
    assertNull(emptyList<RollResult>().toHistoryStats())
  }

  @Test
  fun toHistoryStats_calculatesCoreTotalsIncludingModifiers() {
    val stats =
      listOf(
          RollResult(
            rolls =
              listOf(
                SingleDieRoll(diceType = DiceType.D6, value = 4),
                SingleDieRoll(diceType = DiceType.D20, value = 10),
              ),
            modifier = 2,
          ),
          RollResult(rolls = listOf(SingleDieRoll(diceType = DiceType.D8, value = 8)), modifier = 1),
        )
        .toHistoryStats()

    requireNotNull(stats)
    assertEquals(2, stats.rollCount)
    assertEquals(3, stats.diceRolled)
    assertEquals(12.5, stats.averageTotal)
    assertEquals(9, stats.minTotal)
    assertEquals(16, stats.maxTotal)
  }
}
