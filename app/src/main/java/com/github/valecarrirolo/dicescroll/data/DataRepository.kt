package com.github.valecarrirolo.dicescroll.data

import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import kotlinx.coroutines.flow.Flow

interface DataRepository {
  val rollHistory: Flow<List<RollResult>>
  val selectedDice: Flow<Map<DiceType, Int>>
  val modifier: Flow<Int>

  suspend fun addRoll(roll: RollResult)

  suspend fun clearHistory()

  suspend fun setSelectedDice(selectedDice: Map<DiceType, Int>)

  suspend fun setModifier(modifier: Int)
}
