@file:OptIn(
  ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview

@ThemePreviews
@Composable
fun ModifierControlsSheetPreview() {
  ThemedPreview {
    ModifierControlsSheet(
      modifierValue = 2,
      modifierEnabled = true,
      onModifierEnabledChange = {},
      onSetModifier = {},
      onDismiss = {},
    )
  }
}

@Composable
fun ModifierControlsSheet(
  modifierValue: Int,
  modifierEnabled: Boolean,
  onModifierEnabledChange: (Boolean) -> Unit,
  onSetModifier: (Int) -> Unit,
  onDismiss: () -> Unit,
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(text = "Modifier", fontWeight = FontWeight.Bold, fontSize = 22.sp)
          Text(
            text = if (modifierEnabled) "Applied to the next roll" else "Disabled for rolls",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
          )
        }

        Switch(
          checked = modifierEnabled,
          onCheckedChange = { enabled ->
            onModifierEnabledChange(enabled)
            if (!enabled) onSetModifier(0)
          },
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        ModifierStepButton(
          enabled = modifierEnabled,
          onClick = { onSetModifier(modifierValue - 1) },
          contentDescription = "Decrease modifier",
          icon = { Icon(imageVector = Icons.Default.Remove, contentDescription = null) },
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
          text = modifierValue.asSignedLabel(),
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 38.sp,
        )

        Spacer(modifier = Modifier.width(20.dp))

        ModifierStepButton(
          enabled = modifierEnabled,
          onClick = { onSetModifier(modifierValue + 1) },
          contentDescription = "Increase modifier",
          icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
        )
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(enabled = modifierValue != 0, onClick = { onSetModifier(0) }) { Text("Reset") }
      }
    }
  }
}

@Composable
private fun ModifierStepButton(
  enabled: Boolean,
  onClick: () -> Unit,
  contentDescription: String,
  icon: @Composable () -> Unit,
) {
  IconButton(
    enabled = enabled,
    onClick = onClick,
    modifier =
      Modifier.size(48.dp)
        .semantics { this.contentDescription = contentDescription }
        .background(
          color =
            if (enabled) {
              MaterialTheme.colorScheme.surfaceVariant
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            },
          shape = CircleShape,
        ),
  ) {
    Box(contentAlignment = Alignment.Center) { icon() }
  }
}
