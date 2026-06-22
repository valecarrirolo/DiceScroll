package com.github.valecarrirolo.dicescroll.ui.screens.roller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import com.github.valecarrirolo.dicescroll.ui.components.ModifierChip
import com.github.valecarrirolo.dicescroll.ui.components.RollButton

private val RollControlHeight = 52.dp
private val RollButtonRadius = 26.dp
private val RollControlsGap = 10.dp

@ThemePreviews
@Composable
fun RollControlsPreview() {
  ThemedPreview {
    RollControls(
        state = DiceUiState(selectedDice = mapOf(DiceType.D6 to 2), modifier = 1),
        modifierEnabled = true,
        onModifierClick = {},
        onRoll = {},
        modifier = Modifier.padding(16.dp),
    )
  }
}

@Composable
internal fun RollControls(
    state: DiceUiState,
    modifierEnabled: Boolean,
    onModifierClick: () -> Unit,
    onRoll: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(RollControlsGap),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    ModifierChip(
        modifierValue = state.modifier,
        modifierEnabled = modifierEnabled,
        onClick = onModifierClick,
        modifier = Modifier.height(RollControlHeight),
    )
    RollButton(
        isRolling = state.isRolling,
        totalDiceCount = state.totalDiceCount,
        onRoll = onRoll,
        modifier = Modifier.weight(1f),
    )
  }
}
