package com.napominalka.domain

import com.napominalka.data.model.SessionEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class StatsCalculatorTest {
    @Test
    fun streakCountsConsecutiveDays() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        val sessions = listOf(0, 1, 2).map { offset ->
            val start = today.minusDays(offset.toLong()).atStartOfDay(zone).toInstant().toEpochMilli()
            SessionEntity(startAt = start, endAt = start + 600_000, durationSec = 600, category = "PRACTICE", topicId = null, taskId = null, note = "")
        }
        assertEquals(3, StatsCalculator.streakDays(sessions))
    }
}
