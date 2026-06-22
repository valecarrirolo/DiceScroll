@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.screens.roller

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview
import com.github.valecarrirolo.dicescroll.ui.components.DiceSelectionCard

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
    gridModifier: Modifier? = null,
    scrollEnabled: Boolean = false,
) {
  val containerSize = LocalWindowInfo.current.containerSize
  val density = LocalDensity.current
  val isSmallScreen =
      with(density) {
        (containerSize.height.toDp() < 640.dp) || (containerSize.width.toDp() < 360.dp)
      }

  val poolHeight = if (isSmallScreen) 130.dp else 184.dp
  val cellMinSize = if (isSmallScreen) 60.dp else 76.dp
  val poolGap = if (isSmallScreen) 6.dp else 8.dp
  val poolBottomPadding = if (isSmallScreen) 4.dp else 8.dp
  val poolTitleGap = if (isSmallScreen) 4.dp else 6.dp
  val cardSize = if (isSmallScreen) 56.dp else 72.dp
  val cardTextSize = if (isSmallScreen) 16.sp else 20.sp
  val badgeSize = if (isSmallScreen) 18.dp else 24.dp
  val badgeTextSize = if (isSmallScreen) 9.sp else 11.sp

  val resolvedGridModifier = gridModifier ?: Modifier.height(poolHeight)

  Column(modifier = modifier) {
    Text(
        text = "Dice Pool",
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(poolTitleGap))

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = cellMinSize),
        modifier = resolvedGridModifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = poolBottomPadding),
        horizontalArrangement = Arrangement.spacedBy(poolGap),
        verticalArrangement = Arrangement.spacedBy(poolGap),
        userScrollEnabled = scrollEnabled,
    ) {
      items(DiceType.entries) { type ->
        DiceSelectionCard(
            type = type,
            count = state.selectedDice[type] ?: 0,
            isRecentlyAdded = recentlyAddedDie == type,
            isRecentlyRemoved = recentlyRemovedDie == type,
            modifier = Modifier.testTag("pool-${type.name}"),
            cardSize = cardSize,
            cardTextSize = cardTextSize,
            badgeSize = badgeSize,
            badgeTextSize = badgeTextSize,
            onAdd = { onAddDie(type) },
        )
      }
    }
  }
}
