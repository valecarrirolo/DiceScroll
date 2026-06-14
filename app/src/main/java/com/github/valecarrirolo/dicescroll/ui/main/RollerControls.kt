package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import kotlinx.coroutines.launch

private val TrayPanelRadius = 20.dp
private val EmptyTrayPadding = 20.dp
private val EmptyTrayIconSize = 44.dp
private val EmptyTrayGap = 10.dp
private val RollControlHeight = 52.dp
private val RollButtonRadius = 26.dp
private val RollControlsGap = 10.dp
private val DicePoolHeight = 184.dp
private val DicePoolCellMinSize = 76.dp
private val DicePoolGap = 8.dp
private val DicePoolBottomPadding = 8.dp
private val DicePoolTitleGap = 6.dp

@Composable
internal fun TrayPanel(
  state: DiceUiState,
  highlightedDie: DiceType?,
  onRemoveDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .clip(RoundedCornerShape(TrayPanelRadius))
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
          RoundedCornerShape(TrayPanelRadius),
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
    modifier = Modifier.padding(EmptyTrayPadding),
  ) {
    Icon(
      imageVector = Icons.Default.Info,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
      modifier = Modifier.size(EmptyTrayIconSize),
    )
    Spacer(modifier = Modifier.height(EmptyTrayGap))
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
internal fun RollControls(
  state: DiceUiState,
  modifierEnabled: Boolean,
  onModifierClick: () -> Unit,
  onRoll: () -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(RollControlsGap),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    ModifierChip(
      modifierValue = state.modifier,
      modifierEnabled = modifierEnabled,
      onClick = onModifierClick,
      modifier = Modifier.height(RollControlHeight),
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
    modifier =
      modifier
        .height(RollControlHeight)
        .scale(buttonScale.value)
        .clip(RoundedCornerShape(RollButtonRadius)),
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
internal fun DicePool(
  state: DiceUiState,
  recentlyAddedDie: DiceType?,
  recentlyRemovedDie: DiceType?,
  onAddDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
  gridModifier: Modifier = Modifier.height(DicePoolHeight),
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

    Spacer(modifier = Modifier.height(DicePoolTitleGap))

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = DicePoolCellMinSize),
      modifier = gridModifier.fillMaxWidth(),
      contentPadding = PaddingValues(bottom = DicePoolBottomPadding),
      horizontalArrangement = Arrangement.spacedBy(DicePoolGap),
      verticalArrangement = Arrangement.spacedBy(DicePoolGap),
      userScrollEnabled = scrollEnabled,
    ) {
      items(DiceType.entries) { type ->
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
