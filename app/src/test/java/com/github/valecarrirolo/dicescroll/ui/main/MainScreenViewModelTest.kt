package com.github.valecarrirolo.dicescroll.ui.main

import com.github.valecarrirolo.dicescroll.data.InMemoryDataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

  private val testDispatcher = StandardTestDispatcher()
  private lateinit var repository: InMemoryDataRepository
  private lateinit var viewModel: MainScreenViewModel

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = InMemoryDataRepository()
    viewModel = MainScreenViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun uiState_initialState_hasOneD6() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    testScheduler.advanceUntilIdle()
    val state = viewModel.uiState.value
    assertEquals(1, state.selectedDice[DiceType.D6])
    assertEquals(1, state.totalDiceCount)
  }

  @Test
  fun addDie_increasesCount() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    viewModel.addDie(DiceType.D20)
    testScheduler.advanceUntilIdle()
    val state = viewModel.uiState.value
    assertEquals(1, state.selectedDice[DiceType.D20])
    assertEquals(2, state.totalDiceCount)
  }

  @Test
  fun removeDie_decreasesCount() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    viewModel.addDie(DiceType.D20)
    viewModel.removeDie(DiceType.D20)
    testScheduler.advanceUntilIdle()
    val state = viewModel.uiState.value
    assertTrue(state.selectedDice[DiceType.D20] == null)
    assertEquals(1, state.totalDiceCount)
  }

  @Test
  fun rollTray_generatesResultsAndLogsHistory() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    viewModel.rollTray()
    // Advance time to complete the roll animation delay loop
    testScheduler.advanceUntilIdle()

    val state = viewModel.uiState.value
    assertTrue(state.currentRollResult != null)
    assertEquals(1, state.rollHistory.size)
    assertEquals(state.currentRollResult, state.rollHistory.first())
  }

  @Test
  fun rerollFromHistory_usesSnapshotAndLogsHistory() = runTest {
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    viewModel.addDie(DiceType.D20)
    viewModel.setModifier(3)
    testScheduler.advanceUntilIdle()
    viewModel.rollTray()
    testScheduler.advanceUntilIdle()
    val originalRoll = viewModel.uiState.value.rollHistory.first()

    viewModel.rerollFromHistory(originalRoll)
    testScheduler.advanceUntilIdle()

    val state = viewModel.uiState.value
    val reroll = state.rollHistory.first()
    assertEquals(2, state.rollHistory.size)
    assertEquals(originalRoll.rolls.map { it.diceSnapshot }, reroll.rolls.map { it.diceSnapshot })
    assertEquals(originalRoll.modifier, reroll.modifier)
  }
}
