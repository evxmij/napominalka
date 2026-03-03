package com.napominalka.data.export

import com.napominalka.data.model.SessionEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object CsvExporter {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    fun toCsv(sessions: List<SessionEntity>): String {
        val header = "id,startAt,endAt,durationSec,category,topicId,taskId,note"
        val rows = sessions.joinToString("\n") { s ->
            listOf(
                s.id.toString(),
                millisToIso(s.startAt),
                s.endAt?.let(::millisToIso).orEmpty(),
                s.durationSec.toString(),
                s.category,
                s.topicId?.toString().orEmpty(),
                s.taskId?.toString().orEmpty(),
                escapeCsv(s.note),
            ).joinToString(",")
        }
        return "$header\n$rows"
    }

    private fun millisToIso(ms: Long): String = Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).format(formatter)

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (value.contains(',') || value.contains('"') || value.contains('\n')) "\"$escaped\"" else value
    }
}
