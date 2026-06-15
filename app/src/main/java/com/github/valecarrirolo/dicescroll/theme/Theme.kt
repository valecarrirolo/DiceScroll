@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.foundation.ExperimentalFoundationApi::class,
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
)

package com.github.valecarrirolo.dicescroll.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonPurple,
    secondary = NeonTeal,
    tertiary = NeonPink,
    background = DarkMidnight,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color(0xFF0F0C1B),
    onTertiary = Color.White,
    onBackground = Color(0xFFE2E1E9),
    onSurface = Color(0xFFE2E1E9),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = NeonPurple,
    secondary = Color(0xFF00B4D8), // Deep teal for light mode contrast
    tertiary = NeonPink,
    background = LightMidnight,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F0C1B),
    onSurface = Color(0xFF0F0C1B),
  )

@Composable
fun DiceScrollTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is false by default to preserve the premium neon aesthetics
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun ThemedPreview(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
  DiceScrollTheme { Surface { Column(modifier, content = content) } }
}

@Preview(name = "Light Mode")
@Preview(
  name = "Dark Mode",
  uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class ThemePreviews

@ThemePreviews
@Composable
private fun TestPreview() {
  ThemedPreview { Text("Hello ThemedPreview! 😁") }
}
