package com.napominalka.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SessionScreen(
    paddingValues: PaddingValues,
    category: String,
    note: String,
    startedAtMs: Long?,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRotateCategory: () -> Unit,
    onChangeNote: (String) -> Unit,
) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    androidx.compose.runtime.LaunchedEffect(startedAtMs) {
        while (startedAtMs != null) {
            kotlinx.coroutines.delay(1000)
            now = System.currentTimeMillis()
        }
    }

    val elapsedSec = if (startedAtMs == null) 0 else ((now - startedAtMs) / 1000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Сессия обучения")
        Text("Прошло: ${elapsedSec / 60} мин ${elapsedSec % 60} сек")
        Text("Тип: $category")
        Button(onClick = onRotateCategory) {
            Text("Сменить тип")
        }
        OutlinedTextField(value = note, onValueChange = onChangeNote, label = { Text("Заметка") })
        Button(onClick = onStart, enabled = startedAtMs == null) { Text("Старт") }
        Button(onClick = onStop, enabled = startedAtMs != null) { Text("Стоп") }
    }
}
