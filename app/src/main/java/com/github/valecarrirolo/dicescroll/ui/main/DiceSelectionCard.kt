package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    Box(modifier = Modifier.padding(16.dp)) {
      DiceSelectionCard(type = DiceType.D8, count = 2, onAdd = {})
    }
  }
}

@Composable
fun DiceSelectionCard(
  type: DiceType,
  count: Int,
  isRecentlyAdded: Boolean = false,
  onAdd: () -> Unit,
) {
  val color = Color(type.colorHex.toColorInt())
  val cardScale by
    animateFloatAsState(
      targetValue = if (isRecentlyAdded) 1.06f else 1f,
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "DicePoolSelection",
    )

  Box(
    modifier = Modifier.padding(top = 4.dp, end = 4.dp)
  ) {
    Card(
      modifier =
        Modifier.width(86.dp)
          .scale(cardScale)
          .clickable { onAdd() }
          .border(
            width = 1.dp,
            color = if (count > 0) color else MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(16.dp),
          ),
      shape = RoundedCornerShape(16.dp),
      colors =
        CardDefaults.cardColors(
          containerColor =
            if (count > 0) {
              color.copy(alpha = 0.08f)
            } else {
              MaterialTheme.colorScheme.surface
            }
        ),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Box(
          modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = type.displayName,
            fontWeight = FontWeight.ExtraBold,
            color = color,
            fontSize = 14.sp,
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (count > 0) "Selected" else "Tap to add",
          fontSize = 10.sp,
          textAlign = TextAlign.Center,
          fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
          color =
            if (count > 0) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
          modifier = Modifier.height(14.dp),
        )

        Spacer(modifier = Modifier.height(4.dp))
      }
    }

    if (count > 0) {
      Surface(
        shape = CircleShape,
        color = color,
        contentColor = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier
          .size(24.dp)
          .align(Alignment.TopEnd)
          .offset(x = 4.dp, y = (-4).dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = "$count",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}
