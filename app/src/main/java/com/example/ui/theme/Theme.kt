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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryOrange,
    tertiary = SuccessGreen,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    primaryContainer = PrimaryDark,
    onPrimaryContainer = Color.White,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryOrange,
    tertiary = SuccessGreen,
    background = SoftBg,
    surface = OffWhiteSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = OffBlack,
    onSurface = OffBlack,
    primaryContainer = Color(0xFFDBEAFE), // Blue 100
    onPrimaryContainer = PrimaryDark,
    secondaryContainer = Color(0xFFFFEDD5), // Orange 100
    onSecondaryContainer = Color(0xFFC2410C), // Orange 700
    tertiaryContainer = Color(0xFFD1FAE5), // Emerald 100
    onTertiaryContainer = Color(0xFF047857), // Emerald 700
    outlineVariant = SubtleBorder,
    surfaceVariant = Color(0xFFF1F5F9), // Slate 100
    onSurfaceVariant = OffBlack.copy(alpha = 0.7f)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  textSizeSetting: String = "MEDIUM",
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

  val scale = when (textSizeSetting) {
    "SMALL" -> 0.82f
    "LARGE" -> 1.25f
    else -> 1.0f
  }

  fun scaleTextStyle(style: TextStyle): TextStyle {
    val fontSize = style.fontSize
    val lineHeight = style.lineHeight
    val newFontSize = if (fontSize != TextUnit.Unspecified) {
      (fontSize.value * scale).sp
    } else {
      fontSize
    }
    val newLineHeight = if (lineHeight != TextUnit.Unspecified) {
      (lineHeight.value * scale).sp
    } else {
      lineHeight
    }
    return style.copy(fontSize = newFontSize, lineHeight = newLineHeight)
  }

  val scaledTypography = androidx.compose.material3.Typography(
    displayLarge = scaleTextStyle(Typography.displayLarge),
    displayMedium = scaleTextStyle(Typography.displayMedium),
    displaySmall = scaleTextStyle(Typography.displaySmall),
    headlineLarge = scaleTextStyle(Typography.headlineLarge),
    headlineMedium = scaleTextStyle(Typography.headlineMedium),
    headlineSmall = scaleTextStyle(Typography.headlineSmall),
    titleLarge = scaleTextStyle(Typography.titleLarge),
    titleMedium = scaleTextStyle(Typography.titleMedium),
    titleSmall = scaleTextStyle(Typography.titleSmall),
    bodyLarge = scaleTextStyle(Typography.bodyLarge),
    bodyMedium = scaleTextStyle(Typography.bodyMedium),
    bodySmall = scaleTextStyle(Typography.bodySmall),
    labelLarge = scaleTextStyle(Typography.labelLarge),
    labelMedium = scaleTextStyle(Typography.labelMedium),
    labelSmall = scaleTextStyle(Typography.labelSmall),
  )

  MaterialTheme(colorScheme = colorScheme, typography = scaledTypography, content = content)
}
