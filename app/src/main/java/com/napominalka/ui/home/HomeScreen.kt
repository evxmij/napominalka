package com.napominalka.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.napominalka.MainUiState

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    state: MainUiState,
    onStartSession: () -> Unit,
    onStopSession: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("Сегодня: ${state.todayMinutes} минут")
        Text("Выполнено задач: ${state.tasks.count { it.done }}")
        Button(onClick = onStartSession) { Text("Start") }
        Button(onClick = onStopSession) { Text("Stop") }
    }
}
