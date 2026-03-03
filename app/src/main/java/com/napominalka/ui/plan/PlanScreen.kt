package com.napominalka.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.napominalka.MainUiState
import com.napominalka.data.model.TaskEntity

@Composable
fun PlanScreen(
    paddingValues: PaddingValues,
    state: MainUiState,
    onAddTopic: (String) -> Unit,
    onAddTask: (String) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
) {
    var topicTitle by remember { mutableStateOf("") }
    var taskTitle by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("План")
        OutlinedTextField(value = topicTitle, onValueChange = { topicTitle = it }, label = { Text("Новая тема") })
        Button(onClick = { onAddTopic(topicTitle); topicTitle = "" }) { Text("Добавить тему") }
        OutlinedTextField(value = taskTitle, onValueChange = { taskTitle = it }, label = { Text("Новая задача") })
        Button(onClick = { onAddTask(taskTitle); taskTitle = "" }) { Text("Добавить задачу") }

        Text("Темы: ${state.topics.size}")
        state.topics.forEach { Text("• ${it.title} (${it.status})") }

        Text("Задачи:")
        LazyColumn {
            items(state.tasks, key = { it.id }) { task ->
                Row {
                    Checkbox(checked = task.done, onCheckedChange = { onToggleTask(task) })
                    Text(task.title)
                }
            }
        }
    }
}
