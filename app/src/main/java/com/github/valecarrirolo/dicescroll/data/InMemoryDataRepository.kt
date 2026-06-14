package com.github.valecarrirolo.dicescroll.data

import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryDataRepository : DataRepository {
  private val _rollHistory = MutableStateFlow<List<RollResult>>(emptyList())
  private val _selectedDice = MutableStateFlow<Map<DiceType, Int>>(emptyMap())
  private val _modifier = MutableStateFlow(0)

  override val rollHistory: Flow<List<RollResult>> = _rollHistory.asStateFlow()
  override val selectedDice: Flow<Map<DiceType, Int>> = _selectedDice.asStateFlow()
  override val modifier: Flow<Int> = _modifier.asStateFlow()

  override suspend fun addRoll(roll: RollResult) {
    _rollHistory.update { listOf(roll) + it }
  }

  override suspend fun clearHistory() {
    _rollHistory.value = emptyList()
  }

  override suspend fun setSelectedDice(selectedDice: Map<DiceType, Int>) {
    _selectedDice.value = selectedDice
  }

  override suspend fun setModifier(modifier: Int) {
    _modifier.value = modifier
  }
}
