package com.napominalka.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.napominalka.data.model.DayReviewEntity
import com.napominalka.data.model.SessionEntity
import com.napominalka.data.model.SettingsEntity
import com.napominalka.data.model.TaskEntity
import com.napominalka.data.model.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions ORDER BY startAt DESC")
    fun sessionsFlow(): Flow<List<SessionEntity>>

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReview(review: DayReviewEntity)

    @Query("SELECT * FROM day_reviews ORDER BY date DESC")
    fun reviewFlow(): Flow<List<DayReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopic(topic: TopicEntity): Long

    @Query("SELECT * FROM topics")
    fun topicsFlow(): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM tasks")
    fun tasksFlow(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun getSettings(): SettingsEntity?

    @Query("SELECT * FROM sessions")
    suspend fun allSessions(): List<SessionEntity>

    @Query("SELECT * FROM day_reviews")
    suspend fun allReviews(): List<DayReviewEntity>

    @Query("SELECT * FROM topics")
    suspend fun allTopics(): List<TopicEntity>

    @Query("SELECT * FROM tasks")
    suspend fun allTasks(): List<TaskEntity>

    @Query("DELETE FROM sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM day_reviews")
    suspend fun clearReviews()

    @Query("DELETE FROM topics")
    suspend fun clearTopics()

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()
}
