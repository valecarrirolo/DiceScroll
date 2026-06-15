@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.ui.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.valecarrirolo.dicescroll.theme.NeonPurple
import com.github.valecarrirolo.dicescroll.theme.NeonTeal

private val TabBarHorizontalPadding = 16.dp
private val TabBarBottomPadding = 6.dp
private val TabBarOuterRadius = 18.dp
private val TabBarInnerRadius = 15.dp
private val TabIndicatorHeight = 34.dp
private val TabBarInnerPadding = 3.dp
private val TopBarLogoSize = 32.dp
private val TopBarLogoRadius = 8.dp
private val TopBarTitleGap = 10.dp

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

@Composable
internal fun MainTabs(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
  Surface(
    modifier =
      Modifier.fillMaxWidth()
        .padding(horizontal = TabBarHorizontalPadding)
        .padding(bottom = TabBarBottomPadding),
    shape = RoundedCornerShape(TabBarOuterRadius),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
  ) {
    val tabs = MainTab.entries
    BoxWithConstraints(modifier = Modifier.padding(TabBarInnerPadding).testTag("main-tabs")) {
      val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)
      val indicatorWidth = maxWidth / tabs.size
      val indicatorOffset by
        animateDpAsState(
          targetValue = indicatorWidth * selectedIndex.toFloat(),
          animationSpec = tween(MainMotionTokens.TAB_INDICATOR_MILLIS),
          label = "MainTabIndicator",
        )

      Box(
        modifier =
          Modifier.offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
            .width(indicatorWidth)
            .height(TabIndicatorHeight)
            .clip(RoundedCornerShape(TabBarInnerRadius))
            .background(Brush.linearGradient(colors = listOf(NeonPurple, NeonTeal)))
      )

      Row {
        tabs.forEach { tab ->
          val selected = selectedTab == tab
          Box(
            modifier =
              Modifier.weight(1f)
                .clip(RoundedCornerShape(TabBarInnerRadius))
                .clickable { onTabSelected(tab) }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = tab.name,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
              fontSize = 13.sp,
              color =
                if (selected) {
                  Color.White
                } else {
                  MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
          }
        }
      }
    }
  }
}
