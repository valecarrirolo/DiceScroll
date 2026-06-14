package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.github.valecarrirolo.dicescroll.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.valecarrirolo.dicescroll.data.DefaultDataRepository
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.launch

enum class MainTab {
  Roller,
  History,
}

@OptIn(ExperimentalMaterial3Api::class)
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

@Preview(showBackground = true, name = "Main Screen Light")
@Composable
fun MainScreenContentLightPreview() {
  DiceScrollTheme(darkTheme = false) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1), modifier = 2),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@Preview(showBackground = true, name = "Main Screen Dark")
@Composable
fun MainScreenContentDarkPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(
            selectedDice = mapOf(DiceType.D4 to 1, DiceType.D12 to 1, DiceType.D100 to 1),
            modifier = -1,
          ),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@Preview(showBackground = true, name = "Tray Empty")
@Composable
fun TrayContentEmptyPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth()) {
      MainScreenContent(
        state = DiceUiState(selectedDice = emptyMap()),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@Preview(showBackground = true, name = "Tray Rolling")
@Composable
fun TrayContentRollingPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(
            selectedDice = mapOf(DiceType.D6 to 2),
            isRolling = true,
            animatedValues = listOf(3, 5),
          ),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
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
  var recentlyAddedDie by remember { mutableStateOf<DiceType?>(null) }
  var recentlyRemovedDie by remember { mutableStateOf<DiceType?>(null) }
  var modifierEnabled by remember { mutableStateOf(true) }
  var showModifierSheet by remember { mutableStateOf(false) }

  LaunchedEffect(recentlyAddedDie) {
    if (recentlyAddedDie != null) {
      delay(MainMotion.TRAY_HIGHLIGHT_MILLIS.milliseconds)
      recentlyAddedDie = null
    }
  }

  LaunchedEffect(recentlyRemovedDie) {
    if (recentlyRemovedDie != null) {
      delay(MainMotion.TRAY_HIGHLIGHT_MILLIS.milliseconds)
      recentlyRemovedDie = null
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
    if (selectedTab == MainTab.History) {
      HistoryTabContent(
        state = state,
        onClearHistory = onClearHistory,
        onReroll = onRerollHistory,
        modifier =
          Modifier.fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding(),
      )
    } else {
      val configuration = LocalConfiguration.current
      val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

      if (isLandscape) {
        Row(
          modifier =
            Modifier.fillMaxSize()
              .padding(innerPadding)
              .padding(horizontal = 16.dp)
              .navigationBarsPadding(),
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TrayPanel(
            state = state,
            highlightedDie = recentlyAddedDie,
            onRemoveDie = { type ->
              recentlyRemovedDie = type
              onRemoveDie(type)
            },
            modifier = Modifier.weight(1f).fillMaxHeight().padding(vertical = 8.dp),
          )

          Column(
            modifier = Modifier.weight(1.2f).fillMaxHeight().padding(vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            RollControls(
              state = state,
              modifierEnabled = modifierEnabled,
              onModifierClick = { showModifierSheet = true },
              onRoll = onRoll,
            )

            Spacer(modifier = Modifier.height(8.dp))

            DicePool(
              state = state,
              recentlyAddedDie = recentlyAddedDie,
              recentlyRemovedDie = recentlyRemovedDie,
              onAddDie = { type ->
                recentlyAddedDie = type
                onAddDie(type)
              },
              modifier = Modifier.fillMaxWidth().weight(1f),
              gridModifier = Modifier.weight(1f),
              scrollEnabled = true,
            )
          }
        }
      } else {
        Column(
          modifier =
            Modifier.fillMaxSize()
              .padding(innerPadding)
              .padding(horizontal = 16.dp)
              .navigationBarsPadding(),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          TrayPanel(
            state = state,
            highlightedDie = recentlyAddedDie,
            onRemoveDie = { type ->
              recentlyRemovedDie = type
              onRemoveDie(type)
            },
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp, bottom = 10.dp),
          )

          RollControls(
            state = state,
            modifierEnabled = modifierEnabled,
            onModifierClick = { showModifierSheet = true },
            onRoll = onRoll,
          )

          Spacer(modifier = Modifier.height(12.dp))

          DicePool(
            state = state,
            recentlyAddedDie = recentlyAddedDie,
            recentlyRemovedDie = recentlyRemovedDie,
            onAddDie = { type ->
              if (state.totalDiceCount < 12) {
                recentlyAddedDie = type
                onAddDie(type)
              }
            },
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(onClearTray: () -> Unit) {
  TopAppBar(
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Image(
          painter = painterResource(id = R.mipmap.ic_launcher),
          contentDescription = "DiceScroll Logo",
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp)),
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

@Composable
private fun MainTabs(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
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
          animationSpec = tween(MainMotion.TAB_INDICATOR_MILLIS),
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

@Composable
private fun TrayPanel(
  state: DiceUiState,
  highlightedDie: DiceType?,
  onRemoveDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(20.dp))
        .background(
          Brush.verticalGradient(
            colors =
              listOf(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
              )
          )
        )
        .border(
          1.dp,
          Brush.linearGradient(
            colors = listOf(NeonPurple.copy(alpha = 0.5f), NeonTeal.copy(alpha = 0.5f))
          ),
          RoundedCornerShape(20.dp),
        ),
    contentAlignment = Alignment.Center,
  ) {
    if (state.selectedDice.isEmpty()) {
      EmptyTrayMessage()
    } else {
      TrayContent(state = state, highlightedDie = highlightedDie, onRemoveDie = onRemoveDie)
    }
  }
}

@Composable
private fun EmptyTrayMessage() {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier.padding(20.dp),
  ) {
    Icon(
      imageVector = Icons.Default.Info,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
      modifier = Modifier.size(44.dp),
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(
      text = "Tray is empty",
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
    )
    Text(
      text = "Tap dice below to populate the tray.",
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun RollControls(
  state: DiceUiState,
  modifierEnabled: Boolean,
  onModifierClick: () -> Unit,
  onRoll: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    ModifierChip(
      modifierValue = state.modifier,
      modifierEnabled = modifierEnabled,
      onClick = onModifierClick,
      modifier = Modifier.height(52.dp),
    )
    RollButton(state = state, onRoll = onRoll, modifier = Modifier.weight(1f))
  }
}

@Composable
private fun RollButton(state: DiceUiState, onRoll: () -> Unit, modifier: Modifier = Modifier) {
  val enabled = state.selectedDice.isNotEmpty() && !state.isRolling
  val buttonScale = remember { Animatable(1f) }
  val coroutineScope = rememberCoroutineScope()

  Button(
    onClick = {
      coroutineScope.launch {
        buttonScale.animateTo(0.92f, animationSpec = tween(MainMotion.ROLL_BUTTON_PRESS_MILLIS))
        buttonScale.animateTo(
          1f,
          animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        )
      }
      onRoll()
    },
    enabled = enabled,
    modifier = modifier.height(52.dp).scale(buttonScale.value).clip(RoundedCornerShape(26.dp)),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      ),
    contentPadding = PaddingValues(),
  ) {
    Box(
      modifier =
        Modifier.fillMaxSize()
          .background(
            if (enabled) {
              Brush.linearGradient(colors = listOf(NeonPurple, NeonTeal))
            } else {
              Brush.linearGradient(
                colors =
                  listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.surfaceVariant,
                  )
              )
            }
          ),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text =
          when {
            state.isRolling -> "ROLLING..."
            state.totalDiceCount > 0 -> "ROLL ${state.totalDiceCount} DICE"
            else -> "SELECT DICE"
          },
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun DicePool(
  state: DiceUiState,
  recentlyAddedDie: DiceType?,
  recentlyRemovedDie: DiceType?,
  onAddDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
  gridModifier: Modifier = Modifier.height(184.dp),
  scrollEnabled: Boolean = false,
) {
  Column(modifier = modifier) {
    Text(
      text = "Dice Pool",
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(6.dp))

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 76.dp),
      modifier = gridModifier.fillMaxWidth(),
      contentPadding = PaddingValues(bottom = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      userScrollEnabled = scrollEnabled,
    ) {
      gridItems(DiceType.entries) { type ->
        DiceSelectionCard(
          type = type,
          count = state.selectedDice[type] ?: 0,
          isRecentlyAdded = recentlyAddedDie == type,
          isRecentlyRemoved = recentlyRemovedDie == type,
          modifier = Modifier.testTag("pool-${type.name}"),
          onAdd = { onAddDie(type) },
        )
      }
    }
  }
}
