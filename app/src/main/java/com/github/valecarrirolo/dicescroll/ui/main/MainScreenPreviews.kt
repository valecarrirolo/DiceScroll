package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme

@Preview(showBackground = true, name = "Main Screen Light")
@Composable
fun MainScreenContentLightPreview() {
  DiceScrollTheme(darkTheme = false) {
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

@Preview(showBackground = true, name = "Main Screen Dark")
@Composable
fun MainScreenContentDarkPreview() {
  DiceScrollTheme(darkTheme = true) {
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

@Preview(showBackground = true, name = "Tray Empty")
@Composable
fun TrayContentEmptyPreview() {
  DiceScrollTheme(darkTheme = true) {
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

@Preview(showBackground = true, name = "Tray Rolling")
@Composable
fun TrayContentRollingPreview() {
  DiceScrollTheme(darkTheme = true) {
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

@Preview(showBackground = true, name = "Main Screen Landscape", widthDp = 840, heightDp = 420)
@Composable
fun MainScreenContentLandscapePreview() {
  DiceScrollTheme(darkTheme = true) {
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
