package com.github.valecarrirolo.dicescroll.data

import com.github.valecarrirolo.dicescroll.data.model.RollResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DataRepository {
    val rollHistory: Flow<List<RollResult>>
    suspend fun addRoll(roll: RollResult)
    suspend fun clearHistory()
}

class DefaultDataRepository : DataRepository {
    private val _rollHistory = MutableStateFlow<List<RollResult>>(emptyList())
    override val rollHistory: Flow<List<RollResult>> = _rollHistory.asStateFlow()

    override suspend fun addRoll(roll: RollResult) {
        _rollHistory.update { listOf(roll) + it }
    }

    override suspend fun clearHistory() {
        _rollHistory.value = emptyList()
    }
}
