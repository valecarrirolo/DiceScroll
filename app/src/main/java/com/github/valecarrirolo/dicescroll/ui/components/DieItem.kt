package com.github.valecarrirolo.dicescroll.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import com.github.valecarrirolo.dicescroll.ui.theme.MotionTokens

@ThemePreviews
@Composable
fun DieItemPreview() {
  ThemedPreview {
    Box(modifier = Modifier.padding(16.dp)) {
      DieItem(type = DiceType.D20, value = 18, isRolling = false)
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
          animation = tween(MotionTokens.SHAKE_MILLIS, easing = LinearEasing),
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
          animation = tween(MotionTokens.SHAKE_MILLIS, easing = LinearEasing),
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
        .padding(4.dp)
        .size(72.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(color.copy(alpha = 0.15f))
        .border(2.dp, color, RoundedCornerShape(14.dp))
        .then(clickModifier),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      if (type != null) {
        Text(text = type.displayName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
      }
      Text(
        text = if (value > 0) "$value" else "?",
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color =
          if (value > 0) MaterialTheme.colorScheme.onBackground
          else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
      )
    }
  }
}
