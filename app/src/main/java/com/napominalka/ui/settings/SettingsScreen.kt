package com.napominalka.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
        Text("Настройки")
        Text("Напоминания и тихие часы: базовая инфраструктура готова")
        Text("Экспорт/импорт: реализован data-слой, UI для SAF нужно расширять")
        Text("Тема: используйте system dark/light")
    }
}
