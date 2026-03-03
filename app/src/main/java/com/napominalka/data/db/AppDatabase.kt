package com.napominalka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.napominalka.data.model.DayReviewEntity
import com.napominalka.data.model.SessionEntity
import com.napominalka.data.model.SettingsEntity
import com.napominalka.data.model.TaskEntity
import com.napominalka.data.model.TopicEntity

@Database(
    entities = [SessionEntity::class, DayReviewEntity::class, TopicEntity::class, TaskEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao
}
