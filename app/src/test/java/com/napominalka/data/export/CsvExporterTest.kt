package com.napominalka.data.export

import com.napominalka.data.model.SessionEntity
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExporterTest {
    @Test
    fun exportEscapesQuotesAndCommas() {
        val data = listOf(
            SessionEntity(
                id = 1,
                startAt = 1_700_000_000_000,
                endAt = 1_700_000_100_000,
                durationSec = 100,
                category = "THEORY",
                topicId = null,
                taskId = null,
                note = "note, with \"quote\"",
            ),
        )

        val csv = CsvExporter.toCsv(data)
        assertTrue(csv.contains("\"note, with \"\"quote\"\"\""))
        assertTrue(csv.lines().size >= 2)
    }
}
