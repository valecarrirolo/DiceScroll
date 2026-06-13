package com.github.valecarrirolo.dicescroll.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.valecarrirolo.dicescroll.data.DataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class DiceUiState(
    val selectedDice: Map<DiceType, Int> = emptyMap(),
    val modifier: Int = 0,
    val isRolling: Boolean = false,
    val currentRollResult: RollResult? = null,
    val animatedValues: List<Int> = emptyList(),
    val rollHistory: List<RollResult> = emptyList()
) {
    val totalDiceCount: Int get() = selectedDice.values.sum()
}

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {

    private val _selectedDice = MutableStateFlow<Map<DiceType, Int>>(
        mapOf(DiceType.D6 to 1) // default to 1 D6
    )
    private val _modifier = MutableStateFlow(0)
    private val _isRolling = MutableStateFlow(false)
    private val _currentRollResult = MutableStateFlow<RollResult?>(null)
    private val _animatedValues = MutableStateFlow<List<Int>>(emptyList())

    val uiState: StateFlow<DiceUiState> = combine(
        _selectedDice,
        _modifier,
        _isRolling,
        _currentRollResult,
        _animatedValues,
        repository.rollHistory
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        DiceUiState(
            selectedDice = args[0] as Map<DiceType, Int>,
            modifier = args[1] as Int,
            isRolling = args[2] as Boolean,
            currentRollResult = args[3] as RollResult?,
            animatedValues = args[4] as List<Int>,
            rollHistory = args[5] as List<RollResult>
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DiceUiState()
    )

    fun addDie(diceType: DiceType) {
        if (_isRolling.value) return
        _selectedDice.value = _selectedDice.value.toMutableMap().apply {
            put(diceType, (get(diceType) ?: 0) + 1)
        }
    }

    fun removeDie(diceType: DiceType) {
        if (_isRolling.value) return
        val currentCount = _selectedDice.value[diceType] ?: return
        _selectedDice.value = _selectedDice.value.toMutableMap().apply {
            if (currentCount <= 1) {
                remove(diceType)
            } else {
                put(diceType, currentCount - 1)
            }
        }
    }

    fun setModifier(value: Int) {
        if (_isRolling.value) return
        _modifier.value = value
    }

    fun clearTray() {
        if (_isRolling.value) return
        _selectedDice.value = emptyMap()
        _currentRollResult.value = null
        _animatedValues.value = emptyList()
    }

    fun rollTray() {
        val diceToRoll = _selectedDice.value.flatMap { (type, count) -> List(count) { type } }
        if (diceToRoll.isEmpty() || _isRolling.value) return

        viewModelScope.launch {
            _isRolling.value = true
            _currentRollResult.value = null

            // Roll animation: generate random intermediate values
            val animationSteps = 10
            repeat(animationSteps) { step ->
                _animatedValues.value = diceToRoll.map { type ->
                    Random.nextInt(1, type.maxVal + 1)
                }
                delay(60 + (step * 20).toLong()) // gradually slow down the rolling clatter
            }

            // Final roll
            val finalRolls = diceToRoll.map { type ->
                SingleDieRoll(
                    diceType = type,
                    value = Random.nextInt(1, type.maxVal + 1)
                )
            }

            val result = RollResult(
                rolls = finalRolls,
                modifier = _modifier.value
            )

            repository.addRoll(result)
            _currentRollResult.value = result
            _animatedValues.value = emptyList()
            _isRolling.value = false
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
