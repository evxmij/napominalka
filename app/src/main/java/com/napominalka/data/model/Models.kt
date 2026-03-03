package com.napominalka.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startAt: Long,
    val endAt: Long?,
    val durationSec: Long,
    val category: String,
    val topicId: Long?,
    val taskId: Long?,
    val note: String,
)

@Serializable
@Entity(tableName = "day_reviews")
data class DayReviewEntity(
    @PrimaryKey val date: String,
    val productivity1to10: Int,
    val did: String,
    val blockers: String,
    val planTomorrow: String,
    val totalLearnedSec: Long,
)

@Serializable
@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val module: String?,
    val status: String,
    val tagsCsv: String,
)

@Serializable
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val done: Boolean,
    val priority: String,
    val dueDate: String?,
    val topicId: Long?,
)

@Serializable
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val theme: String = "SYSTEM",
    val weeklyGoalMinutes: Int = 300,
    val remindersEnabled: Boolean = true,
    val reminderScheduleType: String = "DAILY",
    val reminderTime: String = "19:00",
    val reminderDaysMask: Int = 127,
    val smartReminderEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietFrom: String? = "22:00",
    val quietTo: String? = "08:00",
)

@Serializable
data class BackupPayload(
    val schemaVersion: Int = 1,
    val exportedAt: String,
    val sessions: List<SessionEntity>,
    val dayReviews: List<DayReviewEntity>,
    val topics: List<TopicEntity>,
    val tasks: List<TaskEntity>,
    val settings: SettingsEntity,
)
