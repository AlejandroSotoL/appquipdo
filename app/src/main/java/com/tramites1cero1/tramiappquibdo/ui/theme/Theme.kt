package com.tramites1cero1.tramiappquibdo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.tramites1cero1.tramiappquibdo.domain.model.Design

private val AppColorTheme = lightColorScheme(
    primary = primarycolor,
    secondary = White,
    tertiary = Red
)

// Define the specific ColorScheme for Giron Light
val BrandLightColorScheme = lightColorScheme(
    primary = GironColor,
    onPrimary = White, // Corrected from GironColor
    primaryContainer = Gray100,
    secondary = Gray600,
    onSecondary = Gray100,
    background = White,
    onBackground = Gray900,
    surface = surfaceColor,
    onSurface = ColorTextPrimary,
    onSurfaceVariant = Gray600,
    surfaceContainer = ColorBoxModal,
    error = DarkRed,
    onError = LightRed
)

// Define the specific ColorScheme for Giron Dark
val BrandDarkColorScheme = darkColorScheme(
    primary = GironColor,
    onPrimary = White,
    primaryContainer = Gray900,
    secondary = Gray100,
    onSecondary = Gray600,
    background = Gray1000,
    onBackground = Gray300.copy(alpha = 0.87f),
    surface = Gray900,
    onSurface = Gray100.copy(alpha = 0.87f),
    onSurfaceVariant = Gray100,
    surfaceContainer = Gray600,
    error = LightRed,
    onError = Black
)



@Composable
fun AlcaldiasTheme(
    design: Design,
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {

    val baseColorScheme =
        if (darkTheme) BrandDarkColorScheme
        else BrandLightColorScheme

    val colorScheme = baseColorScheme.copy(
        primary = design.primaryColor,
        onPrimary = if (darkTheme) design.onPrimaryColorDark else design.onPrimaryColorLight,
        secondary = if  (darkTheme) design.secondaryColorDark else design.secondaryColor,
        onSecondary = if(darkTheme) Gray100 else design.secondaryColor,
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set the status bar color to transparent to allow the app's
            // background to show through. For Scaffolds, this will be the
            // TopAppBar color or surface color.
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // Set the appearance of the status and navigation bar icons (light or dark)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =!darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =!darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )

}

@Composable
fun InicialTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> AppColorTheme
        else -> AppColorTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}