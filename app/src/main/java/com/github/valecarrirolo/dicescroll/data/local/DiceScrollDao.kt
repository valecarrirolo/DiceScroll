package com.github.valecarrirolo.dicescroll.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DiceScrollDao {
    @Transaction
    @Query("SELECT * FROM roll_results ORDER BY timestamp DESC")
    fun observeRollHistory(): Flow<List<RollResultWithDice>>

    @Query("SELECT * FROM tray_dice ORDER BY dice_type_name")
    fun observeTrayDice(): Flow<List<TrayDieEntity>>

    @Query("SELECT COALESCE((SELECT modifier FROM app_state WHERE id = 0), 0)")
    fun observeModifier(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoll(roll: RollResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDieRolls(rolls: List<SingleDieRollEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setTrayDice(dice: List<TrayDieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setModifier(appState: AppStateEntity)

    @Query("DELETE FROM tray_dice")
    fun clearTrayDice()

    @Query("DELETE FROM roll_results")
    fun clearRollHistory()
}
