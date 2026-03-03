package com.napominalka

import android.app.Application
import androidx.room.Room
import com.napominalka.data.db.AppDatabase
import com.napominalka.data.repo.AppRepository

class NapominalkaApp : Application() {
    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "napominalka.db",
        ).fallbackToDestructiveMigration().build()
        repository = AppRepository(db.dao())
    }
}
