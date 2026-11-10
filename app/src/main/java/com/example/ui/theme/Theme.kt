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

private val LightColorScheme = lightColorScheme(
    primary = MetroNavy,
    onPrimary = Color.White,
    primaryContainer = MetroNavyLight,
    onPrimaryContainer = Color.White,
    secondary = MetroPink,
    onSecondary = Color.White,
    secondaryContainer = MetroPinkContainer,
    onSecondaryContainer = OnMetroPinkContainer,
    tertiary = MetroYellowDark,
    onTertiary = Color.White,
    tertiaryContainer = MetroYellowContainer,
    onTertiaryContainer = MetroYellowDark,
    error = AlarmRed,
    onError = Color.White,
    errorContainer = AlarmRedContainer,
    onErrorContainer = AlarmRedDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = MetroPink,
    onPrimary = Color.White,
    primaryContainer = MetroNavyLight,
    onPrimaryContainer = Color.White,
    secondary = MetroPinkLight,
    onSecondary = Color.Black,
    secondaryContainer = MetroNavyContainer,
    onSecondaryContainer = Color.White,
    tertiary = MetroYellow,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = MetroYellow,
    error = Color(0xFFFF897D),
    onError = Color.Black,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F172A),
    onBackground = Color.White,
    surface = Color(0xFF1E293B),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep Delhi Metro identity distinct by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
