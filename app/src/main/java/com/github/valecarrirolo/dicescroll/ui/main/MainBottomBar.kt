package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DiceScrollBottomBar(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
  NavigationBar {
    NavigationBarItem(
      selected = selectedTab == MainTab.Roller,
      onClick = { onTabSelected(MainTab.Roller) },
      icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Roller") },
      label = { Text("Roller") },
    )
    NavigationBarItem(
      selected = selectedTab == MainTab.History,
      onClick = { onTabSelected(MainTab.History) },
      icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "History") },
      label = { Text("History") },
    )
  }
}
