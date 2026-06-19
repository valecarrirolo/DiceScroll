@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.valecarrirolo.dicescroll.R
import com.github.valecarrirolo.dicescroll.data.DefaultDataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

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
      delay(MainMotionTokens.TRAY_HIGHLIGHT_MILLIS.milliseconds)
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
        HistoryContent(
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

@ThemePreviews
@Composable
fun MainTopBarPreview() {
  ThemedPreview { MainTopBar(onClearTray = {}) }
}

@Composable
internal fun MainTopBar(onClearTray: () -> Unit) {
  TopAppBar(
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_launcher_foreground),
          contentDescription = "DiceScroll Logo",
          modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
        )
        Text(
          text = "DiceScroll",
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    },
    actions = {
      IconButton(onClick = onClearTray) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Clear Tray",
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
  )
}

@ThemePreviews
@Composable
fun MainTabsPreview() {
  ThemedPreview { MainTabs(selectedTab = MainTab.Roller, onTabSelected = {}) }
}

@Composable
internal fun MainTabs(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 6.dp),
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
  ) {
    val tabs = MainTab.entries
    BoxWithConstraints(modifier = Modifier.padding(3.dp).testTag("main-tabs")) {
      val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
      val indicatorWidth = maxWidth / tabs.size
      val indicatorOffset by
        animateDpAsState(
          targetValue = indicatorWidth * selectedIndex.toFloat(),
          animationSpec = tween(MainMotionTokens.TAB_INDICATOR_MILLIS),
          label = "MainTabIndicator",
        )

      Box(
        modifier =
          Modifier.offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
            .width(indicatorWidth)
            .height(34.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.linearGradient(colors = listOf(NeonPurple, NeonTeal)))
      )

      Row {
        tabs.forEach { tab ->
          val selected = selectedTab == tab
          Box(
            modifier =
              Modifier.weight(1f)
                .clip(RoundedCornerShape(15.dp))
                .clickable { onTabSelected(tab) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = tab.name,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
              fontSize = 13.sp,
              color =
                if (selected) {
                  Color.White
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
          }
        }
      }
    }
  }
}
