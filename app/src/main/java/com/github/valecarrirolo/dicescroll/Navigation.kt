package com.github.valecarrirolo.dicescroll

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.github.valecarrirolo.dicescroll.ui.screens.roller.MainScreen
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider { entry<Main> { MainScreen(modifier = Modifier.fillMaxSize()) } },
  )
}
