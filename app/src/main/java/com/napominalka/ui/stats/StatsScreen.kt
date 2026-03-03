package com.napominalka.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.napominalka.MainUiState

@Composable
fun StatsScreen(paddingValues: PaddingValues, state: MainUiState) {
    Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
        Text("Статистика")
        Text("День: ${state.todayMinutes} мин")
        Text("Неделя: ${state.weekMinutes} мин")
        Text("Месяц: ${state.monthMinutes} мин")
        Text("Streak: ${state.streakDays} дней")
        Text("Прогресс по темам/задачам: ${state.completionPercent}%")
        LinearProgressIndicator(progress = { state.completionPercent / 100f })
    }
}
