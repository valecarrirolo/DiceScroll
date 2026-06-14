package com.github.valecarrirolo.dicescroll.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.valecarrirolo.dicescroll.data.DataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceSnapshot
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiceUiState(
  val selectedDice: Map<DiceType, Int> = emptyMap(),
  val modifier: Int = 0,
  val isRolling: Boolean = false,
  val currentRollResult: RollResult? = null,
  val animatedValues: List<Int> = emptyList(),
  val rollHistory: List<RollResult> = emptyList(),
) {
  val totalDiceCount: Int
    get() = selectedDice.values.sum()
}

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {

  private val _selectedDice = MutableStateFlow<Map<DiceType, Int>>(emptyMap())
  private val _modifier = MutableStateFlow(0)
  private val _isRolling = MutableStateFlow(false)
  private val _currentRollResult = MutableStateFlow<RollResult?>(null)
  private val _animatedValues = MutableStateFlow<List<Int>>(emptyList())

  init {
    viewModelScope.launch {
      repository.selectedDice.collect { selectedDice -> _selectedDice.value = selectedDice }
    }
    viewModelScope.launch { repository.modifier.collect { modifier -> _modifier.value = modifier } }
  }

  val uiState: StateFlow<DiceUiState> =
    combine(_selectedDice, _modifier, _isRolling, _currentRollResult, _animatedValues) {
        selectedDice,
        modifier,
        isRolling,
        currentRollResult,
        animatedValues ->
        DiceUiState(
          selectedDice = selectedDice,
          modifier = modifier,
          isRolling = isRolling,
          currentRollResult = currentRollResult,
          animatedValues = animatedValues,
        )
      }
      .let { rollerState ->
        combine(rollerState, repository.rollHistory) { state, rollHistory ->
          state.copy(rollHistory = rollHistory)
        }
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiceUiState(),
      )

  fun addDie(diceType: DiceType) {
    if (_isRolling.value) return
    val updatedDice =
      _selectedDice.value.toMutableMap().apply { put(diceType, (get(diceType) ?: 0) + 1) }
    setSelectedDice(updatedDice)
  }

  fun removeDie(diceType: DiceType) {
    if (_isRolling.value) return
    val currentCount = _selectedDice.value[diceType] ?: return
    val updatedDice =
      _selectedDice.value.toMutableMap().apply {
        if (currentCount <= 1) {
          remove(diceType)
        } else {
          put(diceType, currentCount - 1)
        }
      }
    setSelectedDice(updatedDice)
  }

  fun setModifier(value: Int) {
    if (_isRolling.value) return
    _modifier.value = value
    viewModelScope.launch { repository.setModifier(value) }
  }

  fun clearTray() {
    if (_isRolling.value) return
    setSelectedDice(emptyMap())
    _currentRollResult.value = null
    _animatedValues.value = emptyList()
  }

  fun rollTray() {
    val diceToRoll = _selectedDice.value.flatMap { (type, count) -> List(count) { type } }
    if (diceToRoll.isEmpty() || _isRolling.value) return

    viewModelScope.launch {
      performRoll(
        faces = diceToRoll.map { it.maxVal },
        buildResult = { RollResult(rolls = diceToRoll.rollAll(), modifier = _modifier.value) },
      )
    }
  }

  fun rerollFromHistory(roll: RollResult) {
    if (_isRolling.value || roll.rolls.isEmpty()) return

    viewModelScope.launch {
      val selectedDice = roll.rolls.groupingBy { it.diceType }.eachCount()
      performRoll(
        faces = roll.rolls.map { it.diceSnapshot.safeFaceCount() },
        buildResult = { roll.rerollSnapshot() },
        beforeSave = {
          _modifier.value = roll.modifier
          _selectedDice.value = selectedDice
          repository.setModifier(roll.modifier)
          repository.setSelectedDice(selectedDice)
        },
      )
    }
  }

  fun clearHistory() {
    viewModelScope.launch { repository.clearHistory() }
  }

  private fun setSelectedDice(selectedDice: Map<DiceType, Int>) {
    _selectedDice.value = selectedDice
    _currentRollResult.value = null
    viewModelScope.launch { repository.setSelectedDice(selectedDice) }
  }

  private suspend fun performRoll(
    faces: List<Int>,
    buildResult: () -> RollResult,
    beforeSave: suspend () -> Unit = {},
  ) {
    _isRolling.value = true
    _currentRollResult.value = null

    try {
      animateRoll(faces)
      val result = buildResult()
      beforeSave()
      repository.addRoll(result)
      _currentRollResult.value = result
    } finally {
      _animatedValues.value = emptyList()
      _isRolling.value = false
    }
  }

  private suspend fun animateRoll(faces: List<Int>) {
    repeat(ROLL_ANIMATION_STEPS) { step ->
      _animatedValues.value = faces.map { faceCount -> faceCount.randomDieValue() }
      delay(ROLL_ANIMATION_BASE_DELAY_MS + step * ROLL_ANIMATION_DELAY_STEP_MS)
    }
  }

  private fun List<DiceType>.rollAll(): List<SingleDieRoll> = map { diceType ->
    SingleDieRoll(diceType = diceType, value = diceType.randomValue())
  }

  private fun RollResult.rerollSnapshot(): RollResult =
    RollResult(
      rolls =
        rolls.map { sourceRoll ->
          val snapshot = sourceRoll.diceSnapshot
          SingleDieRoll(
            diceType = sourceRoll.diceType,
            value = snapshot.randomValue(),
            diceSnapshot = snapshot,
          )
        },
      modifier = modifier,
    )

  private fun DiceType.randomValue(): Int = maxVal.randomDieValue()

  private fun DiceSnapshot.randomValue(): Int = safeFaceCount().randomDieValue()

  private fun DiceSnapshot.safeFaceCount(): Int = faces.coerceAtLeast(MIN_DICE_FACE_COUNT)

  private fun Int.randomDieValue(): Int =
    Random.nextInt(from = MIN_DICE_VALUE, until = coerceAtLeast(MIN_DICE_FACE_COUNT) + 1)

  private companion object {
    const val MIN_DICE_VALUE = 1
    const val MIN_DICE_FACE_COUNT = 1
    const val ROLL_ANIMATION_STEPS = 10
    const val ROLL_ANIMATION_BASE_DELAY_MS = 60L
    const val ROLL_ANIMATION_DELAY_STEP_MS = 20L
  }
}
