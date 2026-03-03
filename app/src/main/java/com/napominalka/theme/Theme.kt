package com.napominalka.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun NapominalkaTheme(content: @Composable () -> Unit) {
    val light = lightColorScheme()
    val dark = darkColorScheme()
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) dark else light, content = content)
}
