package com.napominalka.data.repo

import com.napominalka.data.db.AppDao
import com.napominalka.data.model.DayReviewEntity
import com.napominalka.data.model.SessionEntity
import com.napominalka.data.model.SettingsEntity
import com.napominalka.data.model.TaskEntity
import com.napominalka.data.model.TopicEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    fun sessionsFlow(): Flow<List<SessionEntity>> = dao.sessionsFlow()
    fun topicsFlow(): Flow<List<TopicEntity>> = dao.topicsFlow()
    fun tasksFlow(): Flow<List<TaskEntity>> = dao.tasksFlow()
    fun reviewsFlow(): Flow<List<DayReviewEntity>> = dao.reviewFlow()

    suspend fun saveSession(session: SessionEntity) = dao.upsertSession(session)
    suspend fun deleteSession(id: Long) = dao.deleteSession(id)
    suspend fun saveReview(review: DayReviewEntity) = dao.upsertReview(review)
    suspend fun saveTopic(topic: TopicEntity) = dao.upsertTopic(topic)
    suspend fun saveTask(task: TaskEntity) = dao.upsertTask(task)
    suspend fun saveSettings(settings: SettingsEntity) = dao.saveSettings(settings)
    suspend fun getSettings(): SettingsEntity = dao.getSettings() ?: SettingsEntity()

    suspend fun allSessions() = dao.allSessions()
    suspend fun allReviews() = dao.allReviews()
    suspend fun allTopics() = dao.allTopics()
    suspend fun allTasks() = dao.allTasks()

    suspend fun replaceAll(
        sessions: List<SessionEntity>,
        reviews: List<DayReviewEntity>,
        topics: List<TopicEntity>,
        tasks: List<TaskEntity>,
        settings: SettingsEntity,
    ) {
        dao.clearSessions()
        dao.clearReviews()
        dao.clearTopics()
        dao.clearTasks()
        sessions.forEach { dao.upsertSession(it) }
        reviews.forEach { dao.upsertReview(it) }
        topics.forEach { dao.upsertTopic(it) }
        tasks.forEach { dao.upsertTask(it) }
        dao.saveSettings(settings)
    }
}
