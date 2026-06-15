package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme

@Preview(name = "Dice Selection Card")
@Composable
fun DiceSelectionCardPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.padding(16.dp)) {
      DiceSelectionCard(type = DiceType.D8, count = 2, onAdd = {})
    }
  }
}

@Composable
fun DiceSelectionCard(
  type: DiceType,
  count: Int,
  onAdd: () -> Unit,
  modifier: Modifier = Modifier,
  isRecentlyAdded: Boolean = false,
  isRecentlyRemoved: Boolean = false,
) {
  val color = Color(type.colorHex.toColorInt())
  val cardScale by
    animateFloatAsState(
      targetValue =
        when {
          isRecentlyAdded -> 1.08f
          isRecentlyRemoved -> 0.94f
          else -> 1f
        },
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "DicePoolSelection",
    )
  val returnOffset by
    animateDpAsState(
      targetValue = if (isRecentlyRemoved) (-8).dp else 0.dp,
      animationSpec = tween(MainMotionTokens.DICE_FEEDBACK_MILLIS),
      label = "DicePoolReturnOffset",
    )

  Box(
    modifier =
      modifier.padding(top = 10.dp, end = 10.dp).offset {
        IntOffset(x = 0, y = returnOffset.roundToPx())
      }
  ) {
    Card(
      modifier =
        Modifier.size(72.dp)
          .scale(cardScale)
          .clickable { onAdd() }
          .border(
            width = 1.5.dp,
            color = if (count > 0) color else MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(14.dp),
          ),
      shape = RoundedCornerShape(14.dp),
      colors =
        CardDefaults.cardColors(
          containerColor =
            if (count > 0) {
              color.copy(alpha = 0.15f)
            } else {
              MaterialTheme.colorScheme.surface
            }
        ),
    ) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
          text = type.displayName,
          fontWeight = FontWeight.ExtraBold,
          color = if (count > 0) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
          fontSize = 20.sp,
          textAlign = TextAlign.Center,
        )
      }
    }

    if (count > 0) {
      Surface(
        shape = CircleShape,
        color = color,
        contentColor = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier.size(24.dp).align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = "$count",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}
