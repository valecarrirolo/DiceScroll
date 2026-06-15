package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.valecarrirolo.dicescroll.R
import com.github.valecarrirolo.dicescroll.theme.ThemePreviews
import com.github.valecarrirolo.dicescroll.theme.ThemedPreview

private val TopBarLogoSize = 32.dp
private val TopBarLogoRadius = 8.dp
private val TopBarTitleGap = 10.dp

@ThemePreviews
@Composable
fun MainTopBarPreview() {
  ThemedPreview { MainTopBar(onClearTray = {}) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainTopBar(onClearTray: () -> Unit) {
  TopAppBar(
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TopBarTitleGap),
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_launcher_foreground),
          contentDescription = "DiceScroll Logo",
          modifier = Modifier.size(TopBarLogoSize).clip(RoundedCornerShape(TopBarLogoRadius)),
        )
        Text(
          text = "DiceScroll",
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    },
    actions = {
      IconButton(onClick = onClearTray) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Clear Tray",
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
  )
}
