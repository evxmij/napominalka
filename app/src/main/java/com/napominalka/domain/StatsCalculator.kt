package com.napominalka.domain

import com.napominalka.data.model.SessionEntity
import java.time.Instant
import java.time.ZoneId

object StatsCalculator {
    fun streakDays(sessions: List<SessionEntity>): Int {
        val days = sessions
            .map { Instant.ofEpochMilli(it.startAt).atZone(ZoneId.systemDefault()).toLocalDate() }
            .distinct()
            .sortedDescending()

        var streak = 0
        var expected = days.firstOrNull() ?: return 0
        for (day in days) {
            if (day == expected) {
                streak++
                expected = expected.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }
}
