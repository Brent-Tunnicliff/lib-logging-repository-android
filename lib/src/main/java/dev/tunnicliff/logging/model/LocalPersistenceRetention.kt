// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

import java.time.Instant

sealed class LocalPersistenceRetention(
    private val seconds: Long
) {
    private companion object {
        const val SECONDS_IN_MINUTE: Long = 60
        const val SECONDS_IN_HOUR: Long = SECONDS_IN_MINUTE * 60
        const val SECONDS_IN_DAY: Long = SECONDS_IN_HOUR * 24
        const val SECONDS_IN_WEEK: Long = SECONDS_IN_DAY * 7
        const val SECONDS_IN_YEAR: Long = SECONDS_IN_WEEK * 52
        const val SECONDS_IN_MONTH: Long = SECONDS_IN_YEAR / 12
    }

    fun getTimestampFrom(now: Instant): Instant =
        now.minusSeconds(seconds)

    data object ThreeMonths : LocalPersistenceRetention(SECONDS_IN_MONTH * 3)

    @Deprecated("Only meant for development debugging purposes. Do not use in production products.")
    data object FiveMinutes : LocalPersistenceRetention(SECONDS_IN_MINUTE * 5)
}
