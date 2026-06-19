@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview

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
    with(density) { (containerSize.height.toDp() < 640.dp) || (containerSize.width.toDp() < 360.dp) }

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

@ThemePreviews
@Composable
fun DiceSelectionCardPreview() {
  ThemedPreview {
    Box(modifier = Modifier.padding(16.dp)) {
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
  isRecentlyRemoved: Boolean = false,
  cardSize: Dp = 72.dp,
  cardTextSize: TextUnit = 20.sp,
  badgeSize: Dp = 24.dp,
  badgeTextSize: TextUnit = 11.sp,
) {
  val color = Color(type.colorHex.toColorInt())
  val cardScale by
    animateFloatAsState(
      targetValue =
        when {
          isRecentlyAdded -> 1.08f
          isRecentlyRemoved -> 0.94f
          else -> 1f
        },
      animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
      label = "DicePoolSelection",
    )
  val returnOffset by
    animateDpAsState(
      targetValue = if (isRecentlyRemoved) (-8).dp else 0.dp,
      animationSpec = tween(MainMotionTokens.DICE_FEEDBACK_MILLIS),
      label = "DicePoolReturnOffset",
    )

  val paddingOffset = (badgeSize / 2 - 2.dp).coerceAtLeast(0.dp)

  Box(
    modifier =
      modifier.padding(top = paddingOffset, end = paddingOffset).offset {
        IntOffset(x = 0, y = returnOffset.roundToPx())
      }
  ) {
    Card(
      modifier =
        Modifier.size(cardSize)
          .scale(cardScale)
          .clickable { onAdd() }
          .border(
            width = 1.5.dp,
            color = if (count > 0) color else MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(14.dp),
          ),
      shape = RoundedCornerShape(14.dp),
      colors =
        CardDefaults.cardColors(
          containerColor =
            if (count > 0) {
              color.copy(alpha = 0.15f)
            } else {
              MaterialTheme.colorScheme.surface
            }
        ),
    ) {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
          text = type.displayName,
          fontWeight = FontWeight.ExtraBold,
          color = if (count > 0) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
          fontSize = cardTextSize,
          textAlign = TextAlign.Center,
        )
      }
    }

    if (count > 0) {
      Surface(
        shape = CircleShape,
        color = color,
        contentColor = Color.White,
        shadowElevation = 6.dp,
        modifier =
          Modifier.size(badgeSize)
            .align(Alignment.TopEnd)
            .offset(x = badgeSize / 3, y = (-badgeSize / 3)),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(
            text = "$count",
            fontSize = badgeTextSize,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}
