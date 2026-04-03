package com.spitzer.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun CoolRecipesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> CoolRecipesDarkColor
        else -> CoolRecipesLightColor
    }

    CompositionLocalProvider(
        LocalColor provides colorScheme,
        LocalCoolRecipesTypography provides LocalCoolRecipesTypography.current,
        content = content
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = (view.context as Activity)
            val window = context.window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
}

object CoolRecipesTheme {
    val colors: CoolRecipesColor
        @Composable
        get() = LocalColor.current
    val typography: CoolRecipesTypography
        @Composable
        get() = LocalCoolRecipesTypography.current
}
