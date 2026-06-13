package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme

@Preview(name = "Dice Selection Card")
@Composable
fun DiceSelectionCardPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.padding(16.dp).size(86.dp)) {
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
) {
  val color = Color(type.colorHex.toColorInt())
  val cardScale by
    animateFloatAsState(
      targetValue = if (isRecentlyAdded) 1.05f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "DicePoolSelection",
    )

  Card(
    modifier =
      modifier
        .aspectRatio(1.22f)
        .scale(cardScale)
        .clickable { onAdd() }
        .border(
          width = 1.dp,
          color = if (count > 0) color else MaterialTheme.colorScheme.outlineVariant,
          shape = RoundedCornerShape(14.dp),
        ),
    shape = RoundedCornerShape(14.dp),
    colors =
      CardDefaults.cardColors(
        containerColor =
          if (count > 0) {
            color.copy(alpha = 0.12f)
          } else {
            MaterialTheme.colorScheme.surface
          }
      ),
  ) {
    Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
      Text(
        text = type.displayName,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        color = color,
        fontSize = 20.sp,
      )

      if (count > 0) {
        Box(
          modifier =
            Modifier.align(Alignment.TopEnd)
              .size(22.dp)
              .clip(CircleShape)
              .background(color)
              .wrapContentSize(),
          contentAlignment = Alignment.Center,
        ) {
          Text(text = "$count", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
