package com.github.valecarrirolo.dicescroll.data

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import com.github.valecarrirolo.dicescroll.data.local.AppStateEntity
import com.github.valecarrirolo.dicescroll.data.local.DiceScrollDatabase
import com.github.valecarrirolo.dicescroll.data.local.RollResultEntity
import com.github.valecarrirolo.dicescroll.data.local.RollResultWithDice
import com.github.valecarrirolo.dicescroll.data.local.SingleDieRollEntity
import com.github.valecarrirolo.dicescroll.data.local.TrayDieEntity
import com.github.valecarrirolo.dicescroll.data.model.DiceSnapshot
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

interface DataRepository {
  val rollHistory: Flow<List<RollResult>>
  val selectedDice: Flow<Map<DiceType, Int>>
  val modifier: Flow<Int>

  suspend fun addRoll(roll: RollResult)

  suspend fun clearHistory()

  suspend fun setSelectedDice(selectedDice: Map<DiceType, Int>)

  suspend fun setModifier(modifier: Int)
}

class DefaultDataRepository(context: Context) : DataRepository {
  private val database =
    Room.databaseBuilder(
        context.applicationContext,
        DiceScrollDatabase::class.java,
        "dice_scroll.db",
      )
      .build()
  private val dao = database.diceScrollDao()

  override val rollHistory: Flow<List<RollResult>> =
    dao.observeRollHistory().map { entries -> entries.map { it.toDomain() } }
  override val selectedDice: Flow<Map<DiceType, Int>> =
    dao.observeTrayDice().map { dice -> dice.toDomainMap() }
  override val modifier: Flow<Int> = dao.observeModifier()

  override suspend fun addRoll(roll: RollResult) {
    withContext(Dispatchers.IO) {
      database.withTransaction {
        dao.insertRoll(roll.toEntity())
        dao.insertDieRolls(
          roll.rolls.mapIndexed { index, dieRoll ->
            dieRoll.toEntity(rollId = roll.id, sortIndex = index)
          }
        )
      }
    }
  }

  override suspend fun clearHistory() {
    withContext(Dispatchers.IO) { dao.clearRollHistory() }
  }

  override suspend fun setSelectedDice(selectedDice: Map<DiceType, Int>) {
    withContext(Dispatchers.IO) {
      database.withTransaction {
        dao.clearTrayDice()
        dao.setTrayDice(selectedDice.toTrayEntities())
      }
    }
  }

  override suspend fun setModifier(modifier: Int) {
    withContext(Dispatchers.IO) { dao.setModifier(AppStateEntity(0, modifier)) }
  }
}

class InMemoryDataRepository : DataRepository {
  private val _rollHistory = MutableStateFlow<List<RollResult>>(emptyList())
  private val _selectedDice = MutableStateFlow<Map<DiceType, Int>>(mapOf(DiceType.D6 to 1))
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

private fun RollResult.toEntity(): RollResultEntity = RollResultEntity(id, timestamp, modifier)

private fun SingleDieRoll.toEntity(rollId: String, sortIndex: Int): SingleDieRollEntity =
  SingleDieRollEntity(
    id,
    rollId,
    sortIndex,
    diceSnapshot.typeId,
    diceSnapshot.displayName,
    diceSnapshot.faces,
    diceSnapshot.colorHex,
    value,
  )

private fun RollResultWithDice.toDomain(): RollResult =
  RollResult(
    id = roll.id,
    timestamp = roll.timestamp,
    rolls = diceRolls.sortedBy { it.sortIndex }.map { it.toDomain() },
    modifier = roll.modifier,
  )

private fun SingleDieRollEntity.toDomain(): SingleDieRoll =
  SingleDieRoll(
    id = id,
    diceType = DiceType.valueOf(diceTypeName),
    value = value,
    diceSnapshot =
      DiceSnapshot(
        typeId = diceTypeName,
        displayName = diceDisplayName,
        faces = diceFaces,
        colorHex = diceColorHex,
      ),
  )

private fun List<TrayDieEntity>.toDomainMap(): Map<DiceType, Int> =
  mapNotNull { entity ->
      val diceType = runCatching { DiceType.valueOf(entity.diceTypeName) }.getOrNull()
      diceType?.let { it to entity.count }
    }
    .toMap()

private fun Map<DiceType, Int>.toTrayEntities(): List<TrayDieEntity> =
  entries
    .filter { (_, count) -> count > 0 }
    .map { (type, count) -> TrayDieEntity(type.name, count) }
