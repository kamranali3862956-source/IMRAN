package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF68B7DF),
    onPrimary = Color(0xFF003448),
    primaryContainer = Color(0xFF0B4F6C),
    onPrimaryContainer = Color(0xFFD2E8F4),
    secondary = Color(0xFF56D1DE),
    onSecondary = Color(0xFF00363D),
    secondaryContainer = Color(0xFF028090),
    onSecondaryContainer = Color(0xFFBCEBEF),
    tertiary = Color(0xFFFFB5A0),
    background = Color(0xFF0E151A),
    surface = Color(0xFF131C22),
    onBackground = Color(0xFFE2E7EC),
    onSurface = Color(0xFFE2E7EC),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = OceanPrimary,
    onPrimary = OceanOnPrimary,
    primaryContainer = OceanPrimaryContainer,
    onPrimaryContainer = OceanOnPrimaryContainer,
    secondary = TealSecondary,
    onSecondary = TealOnSecondary,
    secondaryContainer = TealSecondaryContainer,
    onSecondaryContainer = TealOnSecondaryContainer,
    tertiary = AmberTertiary,
    onTertiary = AmberOnTertiary,
    tertiaryContainer = AmberTertiaryContainer,
    onTertiaryContainer = AmberOnTertiaryContainer,
    background = Color(0xFFF4F7F9),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF191C1E),
    onSurface = Color(0xFF191C1E),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our brand colors for cohesive identity
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
