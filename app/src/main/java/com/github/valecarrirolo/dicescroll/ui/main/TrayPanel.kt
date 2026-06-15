package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal

private val TrayPanelRadius = 20.dp
private val EmptyTrayPadding = 20.dp
private val EmptyTrayIconSize = 44.dp
private val EmptyTrayGap = 10.dp

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
