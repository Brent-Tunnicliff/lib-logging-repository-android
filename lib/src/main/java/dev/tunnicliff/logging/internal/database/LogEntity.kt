// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.tunnicliff.logging.model.LogLevel
import java.time.Instant
import java.util.UUID

@Entity
internal data class LogEntity(
    // UUID for primary key is probably overly complicated for this use case,
    // but I wanted to do it for the learning experience.
    @PrimaryKey
    val id: UUID,
    val level: LogLevel,
    val message: String,
    val tag: String,
    val timestampCreated: Instant,
    var timestampUpdated: Instant,
    val throwable: Throwable?,
    var uploaded: Boolean
) {
    internal companion object {
        /**
         * Provides a dummy instance for use in previews and tests.
         */
        fun mock(
            id: UUID = UUID.randomUUID(),
            level: LogLevel = LogLevel.DEBUG,
            message: String = "Hello World!",
            tag: String = "LogEntity",
            timestampCreated: Instant = Instant.now(),
            timestampUpdated: Instant = Instant.now(),
            throwable: Throwable? = null,
            uploaded: Boolean = false
        ): LogEntity =
            LogEntity(
                id = id,
                level = level,
                message = message,
                tag = tag,
                timestampCreated = timestampCreated,
                timestampUpdated = timestampUpdated,
                throwable = throwable,
                uploaded = uploaded
            )
    }
}