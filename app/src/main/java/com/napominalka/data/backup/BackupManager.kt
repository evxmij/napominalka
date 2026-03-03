package com.napominalka.data.backup

import android.content.ContentResolver
import android.net.Uri
import com.napominalka.data.model.BackupPayload
import com.napominalka.data.repo.AppRepository
import kotlinx.serialization.json.Json
import java.time.Instant

class BackupManager(
    private val repository: AppRepository,
    private val json: Json = Json { prettyPrint = true; ignoreUnknownKeys = true },
) {
    suspend fun exportTo(contentResolver: ContentResolver, uri: Uri) {
        val payload = BackupPayload(
            exportedAt = Instant.now().toString(),
            sessions = repository.allSessions(),
            dayReviews = repository.allReviews(),
            topics = repository.allTopics(),
            tasks = repository.allTasks(),
            settings = repository.getSettings(),
        )
        contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
            it.write(json.encodeToString(BackupPayload.serializer(), payload))
        }
    }

    suspend fun importFrom(contentResolver: ContentResolver, uri: Uri) {
        val text = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: return
        val payload = json.decodeFromString(BackupPayload.serializer(), text)
        require(payload.schemaVersion == 1) { "Unsupported schema version" }
        repository.replaceAll(payload.sessions, payload.dayReviews, payload.topics, payload.tasks, payload.settings)
    }
}
