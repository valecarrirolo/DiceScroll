package com.github.valecarrirolo.dicescroll.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
}
