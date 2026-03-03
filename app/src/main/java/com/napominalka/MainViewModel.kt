package com.napominalka

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.napominalka.data.model.DayReviewEntity
import com.napominalka.data.model.SessionEntity
import com.napominalka.data.model.TaskEntity
import com.napominalka.data.model.TopicEntity
import com.napominalka.data.repo.AppRepository
import com.napominalka.domain.StatsCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository = (application as NapominalkaApp).repository

    private val _activeStartedAtMs = MutableStateFlow<Long?>(null)
    private val _activeCategory = MutableStateFlow("PRACTICE")
    private val _activeNote = MutableStateFlow("")

    val activeCategory: StateFlow<String> = _activeCategory
    val activeNote: StateFlow<String> = _activeNote
    val activeStartedAtMs: StateFlow<Long?> = _activeStartedAtMs

    val uiState = combine(
        repository.sessionsFlow(),
        repository.topicsFlow(),
        repository.tasksFlow(),
        repository.reviewsFlow(),
    ) { sessions, topics, tasks, reviews ->
        val today = LocalDate.now()
        val todaySec = sessions.filter { it.startAt.toLocalDate() == today }.sumOf { it.durationSec }
        val weekSec = sessions.filter { it.startAt.toLocalDate() >= today.minusDays(6) }.sumOf { it.durationSec }
        val monthSec = sessions.filter { it.startAt.toLocalDate().month == today.month && it.startAt.toLocalDate().year == today.year }
            .sumOf { it.durationSec }
        MainUiState(
            sessions = sessions,
            topics = topics,
            tasks = tasks,
            reviews = reviews.sortedByDescending { it.date },
            todayMinutes = (todaySec / 60).toInt(),
            weekMinutes = (weekSec / 60).toInt(),
            monthMinutes = (monthSec / 60).toInt(),
            streakDays = StatsCalculator.streakDays(sessions),
            completionPercent = calcCompletion(topics, tasks),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun setActiveNote(value: String) {
        _activeNote.value = value
    }

    fun rotateCategory() {
        _activeCategory.value = when (_activeCategory.value) {
            "THEORY" -> "PRACTICE"
            "PRACTICE" -> "PROJECT"
            "PROJECT" -> "DEBUG"
            else -> "THEORY"
        }
    }

    fun startSession() {
        if (_activeStartedAtMs.value == null) {
            _activeStartedAtMs.value = System.currentTimeMillis()
        }
    }

    fun stopSession(topicId: Long? = null, taskId: Long? = null) {
        val started = _activeStartedAtMs.value ?: return
        val end = System.currentTimeMillis()
        val duration = (end - started) / 1_000
        viewModelScope.launch {
            repository.saveSession(
                SessionEntity(
                    startAt = started,
                    endAt = end,
                    durationSec = duration,
                    category = _activeCategory.value,
                    topicId = topicId,
                    taskId = taskId,
                    note = _activeNote.value,
                ),
            )
        }
        _activeStartedAtMs.value = null
        _activeNote.value = ""
    }

    fun deleteSession(id: Long) {
        viewModelScope.launch { repository.deleteSession(id) }
    }

    fun addTopic(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.saveTopic(TopicEntity(title = title, module = null, status = "NOT_STARTED", tagsCsv = "")) }
    }

    fun addTask(title: String, topicId: Long? = null) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.saveTask(TaskEntity(title = title, done = false, priority = "MEDIUM", dueDate = null, topicId = topicId)) }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch { repository.saveTask(task.copy(done = !task.done)) }
    }

    fun saveTodayReview(score: Int, did: String, blockers: String, plan: String, totalSec: Long) {
        val date = LocalDate.now().toString()
        viewModelScope.launch {
            repository.saveReview(
                DayReviewEntity(
                    date = date,
                    productivity1to10 = score.coerceIn(1, 10),
                    did = did,
                    blockers = blockers,
                    planTomorrow = plan,
                    totalLearnedSec = totalSec,
                ),
            )
        }
    }

    private fun calcCompletion(topics: List<TopicEntity>, tasks: List<TaskEntity>): Int {
        if (topics.isEmpty() && tasks.isEmpty()) return 0
        val topicDone = topics.count { it.status == "DONE" }
        val taskDone = tasks.count { it.done }
        val total = topics.size + tasks.size
        return ((topicDone + taskDone) * 100 / total)
    }
}

data class MainUiState(
    val sessions: List<SessionEntity> = emptyList(),
    val topics: List<TopicEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val reviews: List<DayReviewEntity> = emptyList(),
    val todayMinutes: Int = 0,
    val weekMinutes: Int = 0,
    val monthMinutes: Int = 0,
    val streakDays: Int = 0,
    val completionPercent: Int = 0,
)

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
