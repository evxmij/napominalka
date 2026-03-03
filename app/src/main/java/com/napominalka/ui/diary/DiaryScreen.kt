package com.napominalka.ui.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.napominalka.MainUiState

@Composable
fun DiaryScreen(
    paddingValues: PaddingValues,
    state: MainUiState,
    onSaveReview: (score: Int, did: String, blockers: String, plan: String) -> Unit,
) {
    var score by remember { mutableIntStateOf(7) }
    var did by remember { mutableStateOf("") }
    var blockers by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Оценка дня")
        Text("Сегодня обучался: ${state.todayMinutes} минут")
        OutlinedTextField(value = score.toString(), onValueChange = { score = it.toIntOrNull() ?: score }, label = { Text("Оценка 1-10") })
        OutlinedTextField(value = did, onValueChange = { did = it }, label = { Text("Что сделал") })
        OutlinedTextField(value = blockers, onValueChange = { blockers = it }, label = { Text("Что мешало") })
        OutlinedTextField(value = plan, onValueChange = { plan = it }, label = { Text("План на завтра") })
        Button(onClick = { onSaveReview(score, did, blockers, plan) }) { Text("Сохранить") }

        Text("История дневника")
        LazyColumn {
            items(state.reviews, key = { it.date }) { item ->
                Text("${item.date}: ${item.productivity1to10}/10")
            }
        }
    }
}
