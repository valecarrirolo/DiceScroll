package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import com.github.valecarrirolo.dicescroll.theme.DiceScrollTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Preview(showBackground = true, name = "Main Screen Light")
@Composable
fun MainScreenContentLightPreview() {
  DiceScrollTheme(darkTheme = false) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      MainScreenContent(
        state =
          DiceUiState(selectedDice = mapOf(DiceType.D6 to 2, DiceType.D20 to 1), modifier = 2),
        onClearTray = {},
        onShowHistory = {},
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
        onShowHistory = {},
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
        onShowHistory = {},
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
        onShowHistory = {},
        onSetModifier = {},
        onRoll = {},
        onAddDie = {},
        onRemoveDie = {},
      )
    }
  }
}

@Preview(name = "Die Item D20")
@Composable
fun DieItemPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.padding(16.dp)) {
      DieItem(type = DiceType.D20, value = 18, isRolling = false)
    }
  }
}

@Preview(name = "Dice Selection Card")
@Composable
fun DiceSelectionCardPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.padding(16.dp)) {
      DiceSelectionCard(type = DiceType.D8, count = 2, onAdd = {})
    }
  }
}

@Preview(showBackground = true, name = "History Item")
@Composable
fun HistoryItemPreview() {
  DiceScrollTheme(darkTheme = true) {
    Box(modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.background)) {
      HistoryItem(
        roll =
          RollResult(
            rolls =
              listOf(
                SingleDieRoll(diceType = DiceType.D6, value = 4),
                SingleDieRoll(diceType = DiceType.D20, value = 15),
              ),
            modifier = 2,
          ),
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
      )
    }
  }
}
