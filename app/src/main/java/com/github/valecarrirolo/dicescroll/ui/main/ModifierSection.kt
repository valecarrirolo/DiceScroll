package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModifierSection(
  modifierValue: Int,
  expanded: Boolean,
  enabled: Boolean,
  onExpandedChange: (Boolean) -> Unit,
  onEnabledChange: (Boolean) -> Unit,
  onSetModifier: (Int) -> Unit,
) {
  val modifierLabel = "${if (modifierValue >= 0) "+" else ""}$modifierValue"

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    tonalElevation = 0.dp,
  ) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Modifier $modifierLabel",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color =
            if (enabled) {
              MaterialTheme.colorScheme.onSurface
            } else {
              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            },
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Switch(checked = enabled, onCheckedChange = onEnabledChange)
          IconButton(onClick = { onExpandedChange(!expanded) }) {
            Icon(
              imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription =
                if (expanded) "Hide modifier controls" else "Show modifier controls",
            )
          }
        }
      }

      AnimatedVisibility(visible = expanded) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = { onSetModifier(modifierValue - 1) },
            enabled = enabled,
            modifier =
              Modifier.size(36.dp).background(MaterialTheme.colorScheme.surface, CircleShape),
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Minus 1",
              modifier = Modifier.size(18.dp),
            )
          }

          Text(
            text = modifierLabel,
            modifier = Modifier.widthIn(min = 48.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
          )

          IconButton(
            onClick = { onSetModifier(modifierValue + 1) },
            enabled = enabled,
            modifier =
              Modifier.size(36.dp).background(MaterialTheme.colorScheme.surface, CircleShape),
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Plus 1",
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }
    }
  }
}
