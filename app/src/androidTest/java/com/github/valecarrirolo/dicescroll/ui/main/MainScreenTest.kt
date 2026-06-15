package com.github.valecarrirolo.dicescroll.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.valecarrirolo.dicescroll.data.model.DiceType
import com.github.valecarrirolo.dicescroll.data.model.RollResult
import com.github.valecarrirolo.dicescroll.data.model.SingleDieRoll
import java.text.SimpleDateFormat
import java.util.Locale
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.github.valecarrirolo.dicescroll.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent { MainScreen() }
  }

  @Test
  fun appTitle_exists() {
    composeTestRule.onNodeWithText("DiceScroll").assertExists()
  }

  @Test
  fun defaultDicePool_exists() {
    composeTestRule.onNodeWithText("Dice Pool").assertExists()
  }

  @Test
  fun topTabs_replaceHistoryTopIcon() {
    composeTestRule.onNodeWithTag("main-tabs").assertExists()
    composeTestRule.onAllNodesWithContentDescription("View History").assertCountEquals(0)
  }

  @Test
  fun historyTab_opensHistoryContent() {
    composeTestRule.onNodeWithText("History").performClick()

    composeTestRule.onNodeWithText("Roll History").assertExists()
  }

  @Test
  fun modifierChip_opensModifierSheet() {
    composeTestRule.onNodeWithText("MOD +0").performClick()

    composeTestRule.onNodeWithText("Modifier").assertExists()
  }

  @Test
  fun clearTray_disablesRollAction() {
    composeTestRule.onNodeWithContentDescription("Clear Tray").performClick()

    composeTestRule.onNodeWithText("SELECT DICE").assertIsNotEnabled()
  }

  @Test
  fun dicePool_addsAndRemovesMatchingTrayDice() {
    composeTestRule.onNodeWithContentDescription("Clear Tray").performClick()

    composeTestRule.onNodeWithTag("pool-D4").performClick()
    composeTestRule.onAllNodesWithTag("tray-die-D4").assertCountEquals(1)

    composeTestRule.onNodeWithTag("pool-D4").performClick()
    composeTestRule.onAllNodesWithTag("tray-die-D4").assertCountEquals(2)

    composeTestRule.onAllNodesWithTag("tray-die-D4")[0].performClick()
    composeTestRule.onAllNodesWithTag("tray-die-D4").assertCountEquals(1)
  }

  @Test
  fun historyItem_showsSeparatedRollDetails() {
    composeTestRule.setContent {
      HistoryItem(
        roll =
          RollResult(
            timestamp = 1_700_000_000_000,
            rolls =
              listOf(
                SingleDieRoll(diceType = DiceType.D6, value = 4),
                SingleDieRoll(diceType = DiceType.D20, value = 15),
              ),
            modifier = 2,
          ),
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
      )
    }

    composeTestRule.onNodeWithText("1D6, 1D20").assertExists()
    composeTestRule.onNodeWithText("Values: 4, 15   Modifier: +2").assertExists()
    composeTestRule.onNodeWithText("21").assertExists()
    composeTestRule.onNodeWithText("total").assertExists()
    composeTestRule.onNodeWithText("Reroll").assertExists()
  }

  @Test
  fun historyTab_showsStatsSummaryWhenHistoryExists() {
    composeTestRule.setContent {
      HistoryTabContent(
        state =
          DiceUiState(
            rollHistory =
              listOf(
                RollResult(
                  rolls =
                    listOf(
                      SingleDieRoll(diceType = DiceType.D6, value = 4),
                      SingleDieRoll(diceType = DiceType.D20, value = 10),
                    ),
                  modifier = 2,
                ),
                RollResult(
                  rolls = listOf(SingleDieRoll(diceType = DiceType.D8, value = 8)),
                  modifier = 1,
                ),
              )
          ),
        onClearHistory = {},
        onReroll = {},
      )
    }

    composeTestRule.onNodeWithText("Rolls").assertExists()
    composeTestRule.onNodeWithText("Dice").assertExists()
    composeTestRule.onNodeWithText("Avg").assertExists()
    composeTestRule.onNodeWithText("12.5").assertExists()
    composeTestRule.onNodeWithText("Min").assertExists()
    composeTestRule.onAllNodesWithText("9").assertCountEquals(2)
    composeTestRule.onNodeWithText("Max").assertExists()
    composeTestRule.onAllNodesWithText("16").assertCountEquals(2)
  }

  @Test
  fun historyTab_hidesStatsSummaryWhenHistoryIsEmpty() {
    composeTestRule.setContent {
      HistoryTabContent(state = DiceUiState(), onClearHistory = {}, onReroll = {})
    }

    composeTestRule.onNodeWithText("No rolls in this session yet.").assertExists()
    composeTestRule.onNodeWithText("Rolls").assertDoesNotExist()
    composeTestRule.onNodeWithText("Avg").assertDoesNotExist()
  }
}
