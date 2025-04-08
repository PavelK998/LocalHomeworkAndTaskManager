package ru.pkstudio.localhomeworkandtaskmanager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val darkScheme = darkColorScheme(
    primaryContainer = primaryContainer,
    surfaceContainerHighest = primaryContainer,
    background = background,
    onBackground = onBackground,
    surface = background,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    primary = primary,
    secondary = secondary,
    tertiary = tertiary,
    secondaryContainer = secondaryContainer,
    onSecondary = onSecondary,
    onSecondaryContainer = onSecondaryContainer,
    error = error,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    outline = outline

)

private val lightScheme = lightColorScheme(
    primaryContainer = primaryContainerLight,
    surfaceContainerHighest = primaryContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = backgroundLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    primary = primaryLight,
    secondary = secondaryLight,
    tertiary = tertiaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondary = onSecondaryLight,
    onSecondaryContainer = onSecondaryContainerLight,
    onTertiary = onTertiaryLight,
    error = errorLight,
    tertiaryContainer = tertiaryContainerLight,
    outline = outlineLight

)

@Composable
fun LocalHomeworkAndTaskManagerTheme(
    darkTheme: Boolean = false,
    isSystemThemeEnabled: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current

            when {
                isSystemThemeEnabled && isSystemInDarkTheme() -> dynamicDarkColorScheme(context)

                isSystemThemeEnabled && !isSystemInDarkTheme() -> dynamicLightColorScheme(context)

                darkTheme -> dynamicDarkColorScheme(context)

                !darkTheme -> dynamicLightColorScheme(context)

                else -> dynamicLightColorScheme(context)
            }
        }

        isSystemThemeEnabled && isSystemInDarkTheme() -> darkScheme

        isSystemThemeEnabled && !isSystemInDarkTheme() -> lightScheme

        darkTheme -> darkScheme

        !darkTheme -> lightScheme

        else -> lightScheme

    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}