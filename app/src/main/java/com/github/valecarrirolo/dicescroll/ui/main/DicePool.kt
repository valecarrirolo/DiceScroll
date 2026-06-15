package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview

private val DicePoolHeight = 184.dp
private val DicePoolCellMinSize = 76.dp
private val DicePoolGap = 8.dp
private val DicePoolBottomPadding = 8.dp
private val DicePoolTitleGap = 6.dp

@ThemePreviews
@Composable
fun DicePoolPreview() {
  ThemedPreview {
    DicePool(
      state = DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1)),
      recentlyAddedDie = null,
      recentlyRemovedDie = null,
      onAddDie = {},
    )
  }
}

@Composable
internal fun DicePool(
  state: DiceUiState,
  recentlyAddedDie: DiceType?,
  recentlyRemovedDie: DiceType?,
  onAddDie: (DiceType) -> Unit,
  modifier: Modifier = Modifier,
  gridModifier: Modifier = Modifier.height(DicePoolHeight),
  scrollEnabled: Boolean = false,
) {
  Column(modifier = modifier) {
    Text(
      text = "Dice Pool",
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp,
      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(DicePoolTitleGap))

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = DicePoolCellMinSize),
      modifier = gridModifier.fillMaxWidth(),
      contentPadding = PaddingValues(bottom = DicePoolBottomPadding),
      horizontalArrangement = Arrangement.spacedBy(DicePoolGap),
      verticalArrangement = Arrangement.spacedBy(DicePoolGap),
      userScrollEnabled = scrollEnabled,
    ) {
      items(DiceType.entries) { type ->
        DiceSelectionCard(
          type = type,
          count = state.selectedDice[type] ?: 0,
          isRecentlyAdded = recentlyAddedDie == type,
          isRecentlyRemoved = recentlyRemovedDie == type,
          modifier = Modifier.testTag("pool-${type.name}"),
          onAdd = { onAddDie(type) },
        )
      }
    }
  }
}
