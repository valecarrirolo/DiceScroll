package com.github.valecarrirolo.dicescroll.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview

@ThemePreviews
@Composable
fun ModifierChipPreview() {
  ThemedPreview {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
      ModifierChip(modifierValue = 3, modifierEnabled = true, onClick = {})
      ModifierChip(modifierValue = 0, modifierEnabled = false, onClick = {})
    }
  }
}

@Composable
fun ModifierChip(
  modifierValue: Int,
  modifierEnabled: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val label = if (modifierEnabled) "MOD ${modifierValue.asSignedLabel()}" else "MOD OFF"

  Surface(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(18.dp),
    color =
      if (modifierEnabled) {
        MaterialTheme.colorScheme.primaryContainer
      } else {
        MaterialTheme.colorScheme.surfaceVariant
      },
  ) {
    Box(
      modifier = Modifier.padding(horizontal = 12.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = label,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color =
          if (modifierEnabled) {
            MaterialTheme.colorScheme.onPrimaryContainer
          } else {
            MaterialTheme.colorScheme.onSurfaceVariant
          },
      )
    }
  }
}

fun Int.asSignedLabel(): String = "${if (this >= 0) "+" else ""}$this"
