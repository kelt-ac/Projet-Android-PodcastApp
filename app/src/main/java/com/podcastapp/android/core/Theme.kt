package com.podcastapp.android.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Couleurs inspirées du design SoundArchive ──────────────────
val PrimaryDark    = Color(0xFF1A0F6E)   // Bleu marine foncé
val PrimaryMedium  = Color(0xFF3D3DBF)   // Bleu moyen
val PrimaryLight   = Color(0xFFEEEDFE)   // Violet très clair
val AccentPurple   = Color(0xFF6650A4)   // Violet Material
val BackgroundLight= Color(0xFFF0EEF8)   // Fond clair violet
val SurfaceLight   = Color(0xFFFFFFFF)   // Blanc pur
val TextPrimary    = Color(0xFF1C1C1C)   // Texte principal
val TextSecondary  = Color(0xFF888888)   // Texte secondaire
val ErrorColor     = Color(0xFFB3261E)   // Rouge erreur
val FacebookBlue   = Color(0xFF1877F2)   // Bleu Facebook

private val LightColors = lightColorScheme(
    primary          = PrimaryDark,
    onPrimary        = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary        = PrimaryMedium,
    onSecondary      = Color.White,
    background       = BackgroundLight,
    onBackground     = TextPrimary,
    surface          = SurfaceLight,
    onSurface        = TextPrimary,
    error            = ErrorColor,
    onError          = Color.White,
)

private val DarkColors = darkColorScheme(
    primary          = PrimaryLight,
    onPrimary        = PrimaryDark,
    primaryContainer = PrimaryMedium,
    onPrimaryContainer = Color.White,
    secondary        = PrimaryMedium,
    onSecondary      = Color.White,
    background       = Color(0xFF121212),
    onBackground     = Color.White,
    surface          = Color(0xFF1E1E2E),
    onSurface        = Color.White,
    error            = ErrorColor,
    onError          = Color.White,
)

@Composable
fun PodcastAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content     = content
    )
}