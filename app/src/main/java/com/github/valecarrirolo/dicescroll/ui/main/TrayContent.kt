package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal

@Composable
fun TrayContent(state: DiceUiState, highlightedDie: DiceType?, onRemoveDie: (DiceType) -> Unit) {
  val itemsToDisplay =
    remember(state.isRolling, state.animatedValues, state.currentRollResult) {
      if (state.isRolling) {
        state.animatedValues.map { Pair(null, it) }
      } else if (state.currentRollResult != null) {
        state.currentRollResult.rolls.map { Pair(it.diceType, it.value) }
      } else {
        state.selectedDice.flatMap { (type, count) -> List(count) { Pair(type, 0) } }
      }
    }

  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
      AnimatedContent(
        targetState = Pair(state.isRolling, state.currentRollResult),
        transitionSpec = {
          fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
            fadeOut(animationSpec = tween(90))
        },
        label = "TotalDisplay",
      ) { (rolling, result) ->
        if (rolling) {
          Text(
            text = "Rolling...",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
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
              fontSize = 36.sp,
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
        columns = GridCells.Adaptive(minSize = 84.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        gridItems(itemsToDisplay) { (type, value) ->
          Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            DieItem(
              type = type,
              value = value,
              isRolling = state.isRolling,
              isHighlighted = type == highlightedDie,
              onClick = type?.let { { onRemoveDie(it) } },
            )
          }
        }
      }
    }
  }
}

@Composable
fun DieItem(
  type: DiceType?,
  value: Int,
  isRolling: Boolean,
  isHighlighted: Boolean = false,
  onClick: (() -> Unit)? = null,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "ShakeAnim")
  val rotation by
    infiniteTransition.animateFloat(
      initialValue = -15f,
      targetValue = 15f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(100, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "ShakeRotate",
    )

  val scale by
    infiniteTransition.animateFloat(
      initialValue = 0.9f,
      targetValue = 1.1f,
      animationSpec =
        infiniteRepeatable(
          animation = tween(100, easing = LinearEasing),
          repeatMode = RepeatMode.Reverse,
        ),
      label = "ShakeScale",
    )

  val animationModifier =
    if (isRolling) {
      Modifier.graphicsLayer {
        rotationZ = rotation
        scaleX = scale
        scaleY = scale
      }
    } else {
      Modifier
    }

  val clickModifier =
    if (!isRolling && type != null && onClick != null) {
      Modifier.clickable { onClick() }
    } else {
      Modifier
    }

  val highlightScale by
    animateFloatAsState(
      targetValue = if (!isRolling && isHighlighted) 1.08f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "TrayDieHighlight",
    )

  val color = type?.colorHex?.let { Color(it.toColorInt()) } ?: NeonPurple

  Box(
    modifier =
      animationModifier
        .scale(highlightScale)
        .padding(8.dp)
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(color.copy(alpha = 0.15f))
        .border(2.dp, color, RoundedCornerShape(16.dp))
        .then(clickModifier),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      if (type != null) {
        Text(text = type.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
      }
      Text(
        text = if (value > 0) "$value" else "?",
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color =
          if (value > 0) MaterialTheme.colorScheme.onBackground
          else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
      )
    }
  }
}
