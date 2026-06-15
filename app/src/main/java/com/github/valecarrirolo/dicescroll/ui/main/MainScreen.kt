@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.valecarrirolo.dicescroll.data.DefaultDataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme

@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = defaultMainScreenViewModel(),
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val haptic = LocalHapticFeedback.current
  var selectedTab by remember { mutableStateOf(MainTab.Roller) }

  LaunchedEffect(state.isRolling) {
    if (state.isRolling || state.currentRollResult != null) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  MainScreenContent(
    state = state,
    selectedTab = selectedTab,
    onTabSelected = { selectedTab = it },
    onClearTray = { viewModel.clearTray() },
    onClearHistory = { viewModel.clearHistory() },
    onRerollHistory = { roll ->
      viewModel.rerollFromHistory(roll)
      selectedTab = MainTab.Roller
    },
    onSetModifier = { viewModel.setModifier(it) },
    onRoll = { viewModel.rollTray() },
    onAddDie = { viewModel.addDie(it) },
    onRemoveDie = { viewModel.removeDie(it) },
    modifier = modifier,
  )
}

@Composable
private fun defaultMainScreenViewModel(): MainScreenViewModel {
  val context = LocalContext.current.applicationContext
  return viewModel { MainScreenViewModel(DefaultDataRepository(context)) }
}

@ThemePreviews
@Composable
fun MainScreenPreview() {
  ThemedPreview {
    MainScreenContent(
      state = DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1), modifier = 2),
      onClearTray = {},
      onSetModifier = {},
      onRoll = {},
      onAddDie = {},
      onRemoveDie = {},
    )
  }
}
