package com.ruchitgoud.trackmyspend.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeonYellow,
    secondary = NeonPink,
    tertiary = NeonMint,
    background = DarkBg,
    surface = DarkCard,
    onPrimary = BrutalistBlack,
    onSecondary = BrutalistWhite,
    onTertiary = BrutalistBlack,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = BrutalistWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BrutalistWhite,
    surface = BrutalistWhite,
    onBackground = BrutalistBlack,
    onSurface = BrutalistBlack,
    outline = BrutalistBlack
)

@Composable
fun TrackMySpendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to prevent "ashy" tinting
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
