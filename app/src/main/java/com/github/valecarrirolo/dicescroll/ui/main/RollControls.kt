package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import kotlinx.coroutines.launch

private val RollControlHeight = 52.dp
private val RollButtonRadius = 26.dp
private val RollControlsGap = 10.dp

@Composable
internal fun RollControls(
  state: DiceUiState,
  modifierEnabled: Boolean,
  onModifierClick: () -> Unit,
  onRoll: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
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
        buttonScale.animateTo(
          0.92f,
          animationSpec = tween(MainMotionTokens.ROLL_BUTTON_PRESS_MILLIS),
        )
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
