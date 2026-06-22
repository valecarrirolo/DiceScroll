package com.github.valecarrirolo.dicescroll.ui.components

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import com.github.valecarrirolo.dicescroll.ui.theme.MotionTokens

@ThemePreviews
@Composable
fun DiceSelectionCardPreview() {
  ThemedPreview {
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
  cardSize: Dp = 72.dp,
  cardTextSize: TextUnit = 20.sp,
  badgeSize: Dp = 24.dp,
  badgeTextSize: TextUnit = 11.sp,
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
      animationSpec = tween(MotionTokens.DICE_FEEDBACK_MILLIS),
      label = "DicePoolReturnOffset",
    )

  val paddingOffset = (badgeSize / 2 - 2.dp).coerceAtLeast(0.dp)

  Box(
    modifier =
      modifier.padding(top = paddingOffset, end = paddingOffset).offset {
        IntOffset(x = 0, y = returnOffset.roundToPx())
      }
  ) {
    Card(
      modifier =
        Modifier.size(cardSize)
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
          fontSize = cardTextSize,
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
        modifier =
          Modifier.size(badgeSize)
            .align(Alignment.TopEnd)
            .offset(x = badgeSize / 3, y = (-badgeSize / 3)),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = "$count",
            fontSize = badgeTextSize,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}
