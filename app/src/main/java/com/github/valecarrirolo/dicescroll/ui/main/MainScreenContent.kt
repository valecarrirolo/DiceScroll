@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

private const val MAX_TRAY_DICE = 12
private val ScreenHorizontalPadding = 16.dp
private val ScreenBottomSpacer = 12.dp
private val LandscapeGap = 12.dp
private val LandscapeVerticalPadding = 8.dp
private val PortraitTrayBottomPadding = 10.dp
private val PortraitTrayTopPadding = 8.dp
private const val LandscapeTrayWeight = 1.85f
private const val LandscapeControlsWeight = 1f

private data class DiceFeedback(val added: DiceType? = null, val removed: DiceType? = null)

@Composable
fun MainScreenContent(
  state: DiceUiState,
  onClearTray: () -> Unit,
  onSetModifier: (Int) -> Unit,
  onRoll: () -> Unit,
  onAddDie: (DiceType) -> Unit,
  onRemoveDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
  selectedTab: MainTab = MainTab.Roller,
  onTabSelected: (MainTab) -> Unit = {},
  onClearHistory: () -> Unit = {},
  onRerollHistory: (RollResult) -> Unit = {},
) {
  var diceFeedback by remember { mutableStateOf(DiceFeedback()) }
  var modifierEnabled by remember { mutableStateOf(true) }
  var showModifierSheet by remember { mutableStateOf(false) }

  LaunchedEffect(diceFeedback) {
    if (diceFeedback.added != null || diceFeedback.removed != null) {
      delay(MainMotion.TRAY_HIGHLIGHT_MILLIS.milliseconds)
      diceFeedback = DiceFeedback()
    }
  }

  if (showModifierSheet) {
    ModifierControlsSheet(
      modifierValue = state.modifier,
      modifierEnabled = modifierEnabled,
      onModifierEnabledChange = { modifierEnabled = it },
      onSetModifier = onSetModifier,
      onDismiss = { showModifierSheet = false },
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      Column {
        MainTopBar(onClearTray = onClearTray)
        MainTabs(selectedTab = selectedTab, onTabSelected = onTabSelected)
      }
    },
  ) { innerPadding ->
    val screenModifier =
      Modifier.fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = ScreenHorizontalPadding)
        .navigationBarsPadding()

    when (selectedTab) {
      MainTab.History ->
        HistoryTabContent(
          state = state,
          onClearHistory = onClearHistory,
          onReroll = onRerollHistory,
          modifier = screenModifier,
        )
      MainTab.Roller -> {
        val orientation = LocalConfiguration.current.orientation
        val onAddDieFromPool: (DiceType) -> Unit = { type ->
          if (state.totalDiceCount < MAX_TRAY_DICE) {
            diceFeedback = DiceFeedback(added = type)
            onAddDie(type)
          }
        }
        val onRemoveDieFromTray: (DiceType) -> Unit = { type ->
          diceFeedback = DiceFeedback(removed = type)
          onRemoveDie(type)
        }

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
          LandscapeRollerContent(
            state = state,
            diceFeedback = diceFeedback,
            modifierEnabled = modifierEnabled,
            onModifierClick = { showModifierSheet = true },
            onRoll = onRoll,
            onAddDie = onAddDieFromPool,
            onRemoveDie = onRemoveDieFromTray,
            modifier = screenModifier,
          )
        } else {
          PortraitRollerContent(
            state = state,
            diceFeedback = diceFeedback,
            modifierEnabled = modifierEnabled,
            onModifierClick = { showModifierSheet = true },
            onRoll = onRoll,
            onAddDie = onAddDieFromPool,
            onRemoveDie = onRemoveDieFromTray,
            modifier = screenModifier,
          )
        }
      }
    }
  }
}

@Composable
private fun PortraitRollerContent(
  state: DiceUiState,
  diceFeedback: DiceFeedback,
  modifierEnabled: Boolean,
  onModifierClick: () -> Unit,
  onRoll: () -> Unit,
  onAddDie: (DiceType) -> Unit,
  onRemoveDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    TrayPanel(
      state = state,
      highlightedDie = diceFeedback.added,
      onRemoveDie = onRemoveDie,
      modifier =
        Modifier.weight(1f)
          .fillMaxWidth()
          .padding(top = PortraitTrayTopPadding, bottom = PortraitTrayBottomPadding),
    )

    RollControls(
      state = state,
      modifierEnabled = modifierEnabled,
      onModifierClick = onModifierClick,
      onRoll = onRoll,
    )

    Spacer(modifier = Modifier.height(ScreenBottomSpacer))

    DicePool(
      state = state,
      recentlyAddedDie = diceFeedback.added,
      recentlyRemovedDie = diceFeedback.removed,
      onAddDie = onAddDie,
    )
  }
}

@Composable
private fun LandscapeRollerContent(
  state: DiceUiState,
  diceFeedback: DiceFeedback,
  modifierEnabled: Boolean,
  onModifierClick: () -> Unit,
  onRoll: () -> Unit,
  onAddDie: (DiceType) -> Unit,
  onRemoveDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(LandscapeGap),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TrayPanel(
      state = state,
      highlightedDie = diceFeedback.added,
      onRemoveDie = onRemoveDie,
      modifier =
        Modifier.weight(LandscapeTrayWeight)
          .fillMaxHeight()
          .padding(vertical = LandscapeVerticalPadding),
    )

    Column(
      modifier =
        Modifier.weight(LandscapeControlsWeight)
          .fillMaxHeight()
          .padding(vertical = LandscapeVerticalPadding),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      RollControls(
        state = state,
        modifierEnabled = modifierEnabled,
        onModifierClick = onModifierClick,
        onRoll = onRoll,
      )

      Spacer(modifier = Modifier.height(LandscapeVerticalPadding))

      DicePool(
        state = state,
        recentlyAddedDie = diceFeedback.added,
        recentlyRemovedDie = diceFeedback.removed,
        onAddDie = onAddDie,
        modifier = Modifier.fillMaxWidth().weight(1f),
        gridModifier = Modifier.weight(1f),
        scrollEnabled = true,
      )
    }
  }
}
