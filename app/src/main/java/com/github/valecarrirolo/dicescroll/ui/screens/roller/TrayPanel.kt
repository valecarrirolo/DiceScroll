@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.screens.roller

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import com.github.valecarrirolo.dicescroll.ui.components.DieItem
import com.github.valecarrirolo.dicescroll.ui.theme.MotionTokens

private val TrayPanelRadius = 20.dp
private val EmptyTrayPadding = 20.dp
private val EmptyTrayIconSize = 44.dp
private val EmptyTrayGap = 10.dp

data class TrayDieInstance(val key: String, val type: DiceType?, val value: Int)

@ThemePreviews
@Composable
fun TrayPanelPreview() {
  ThemedPreview {
    TrayPanel(
        state = DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1)),
        highlightedDie = null,
        onRemoveDie = {},
        modifier = Modifier.size(300.dp, 200.dp).padding(16.dp),
    )
  }
}

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
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Tap dice below to populate the tray.",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
    )
  }
}

@ThemePreviews
@Composable
fun TrayContentPreview() {
  ThemedPreview {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).size(300.dp)) {
      TrayContent(
          state = DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1)),
          highlightedDie = null,
          onRemoveDie = {},
      )
    }
  }
}

@Composable
fun TrayContent(state: DiceUiState, highlightedDie: DiceType?, onRemoveDie: (DiceType) -> Unit) {
  val itemsToDisplay =
      remember(state.isRolling, state.animatedValues, state.currentRollResult, state.selectedDice) {
        if (state.isRolling) {
          state.animatedValues.mapIndexed { index, value ->
            TrayDieInstance(key = "anim_$index", type = null, value = value)
          }
        } else if (state.currentRollResult != null) {
          state.currentRollResult.rolls.mapIndexed { index, roll ->
            TrayDieInstance(key = roll.id, type = roll.diceType, value = roll.value)
          }
        } else {
          val indexMap = mutableMapOf<DiceType, Int>()
          state.selectedDice.flatMap { (type, count) ->
            List(count) {
              val index = indexMap.getOrDefault(type, 0)
              indexMap[type] = index + 1
              TrayDieInstance(key = "${type.name}_$index", type = type, value = 0)
            }
          }
        }
      }

  Column(
      modifier = Modifier.fillMaxSize().padding(10.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(modifier = Modifier.fillMaxWidth().height(66.dp), contentAlignment = Alignment.Center) {
      AnimatedContent(
          targetState = Pair(state.isRolling, state.currentRollResult),
          transitionSpec = {
            fadeIn(
                animationSpec = tween(MotionTokens.TOTAL_FADE_IN_MILLIS, delayMillis = 90)
            ) togetherWith fadeOut(animationSpec = tween(MotionTokens.TOTAL_FADE_OUT_MILLIS))
          },
          label = "TotalDisplay",
      ) { (rolling, result) ->
        if (rolling) {
          Text(
              text = "Rolling...",
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              fontFamily = FontFamily.Monospace,
              color = NeonTeal,
          )
        } else if (result != null) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TOTAL",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            )
            Text(
                text = "${result.total}",
                fontWeight = FontWeight.Black,
                fontSize = 34.sp,
                fontFamily = FontFamily.Monospace,
                color = NeonTeal,
            )
          }
        } else {
          Text(
              text = "Ready to Roll",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
          )
        }
      }
    }

    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
      LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 76.dp),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
          userScrollEnabled = false,
      ) {
        items(items = itemsToDisplay, key = { it.key }) { dieInstance ->
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .animateItem()
                      .testTag("tray-die-${dieInstance.type?.name ?: "ROLLING"}"),
              contentAlignment = Alignment.Center,
          ) {
            DieItem(
                type = dieInstance.type,
                value = dieInstance.value,
                isRolling = state.isRolling,
                isHighlighted = dieInstance.type == highlightedDie,
                onClick = dieInstance.type?.let { { onRemoveDie(it) } },
            )
          }
        }
      }
    }
  }
}
