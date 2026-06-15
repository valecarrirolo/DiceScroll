@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview

@ThemePreviews
@Composable
fun MainScreenContentLightPreview() {
  ThemedPreview {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1), modifier = 2),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@ThemePreviews
@Composable
fun MainScreenContentDarkPreview() {
  ThemedPreview {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(
            selectedDice = mapOf(DiceType.D4 to 1, DiceType.D12 to 1, DiceType.D100 to 1),
            modifier = -1,
          ),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@ThemePreviews
@Composable
fun TrayContentEmptyPreview() {
  ThemedPreview {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth()) {
      MainScreenContent(
        state = DiceUiState(selectedDice = emptyMap()),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@ThemePreviews
@Composable
fun TrayContentRollingPreview() {
  ThemedPreview {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(
            selectedDice = mapOf(DiceType.D6 to 2),
            isRolling = true,
            animatedValues = listOf(3, 5),
          ),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@ThemePreviews
@Preview(showBackground = true, name = "Main Screen Landscape", widthDp = 840, heightDp = 420)
@Composable
fun MainScreenContentLandscapePreview() {
  ThemedPreview {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(
            selectedDice = mapOf(DiceType.D4 to 2, DiceType.D6 to 3, DiceType.D20 to 1),
            modifier = 1,
          ),
        onClearTray = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}
