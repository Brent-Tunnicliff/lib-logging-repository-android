// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.serialization.Serializable
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
    companion object {
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

    /**
     * Simple mapping of throwable to make storing simpler.
     */
    @Serializable
    data class Throwable(
        val type: String,
        val message: String?,
        val cause: Throwable?
    ) {
        companion object {
            fun mock(
                type: String = "kotlin.Throwable",
                message: String? = "This is a message",
                cause: Throwable? = null,
            ): Throwable =
                Throwable(
                    type = type,
                    message = message,
                    cause = cause
                )
        }

        override fun toString(): String =
            "$type: $message"
    }
}

internal fun Throwable.toEntity(): LogEntity.Throwable =
    LogEntity.Throwable(
        type = this::class.qualifiedName ?: this::class.simpleName ?: "Unknown",
        message = message,
        cause = cause?.toEntity()
    )

internal fun LogEntity.Throwable.flatCauses(): List<LogEntity.Throwable> {
    if (cause == null) {
        return emptyList()
    }

    val nextCauses = cause.flatCauses()
    return listOf(cause) + nextCauses
}
