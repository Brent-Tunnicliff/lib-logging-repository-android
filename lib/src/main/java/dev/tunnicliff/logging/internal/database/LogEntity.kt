// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.tunnicliff.logging.BuildConfig
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Entity
internal data class LogEntity(
    @PrimaryKey
    val id: UUID,
    val level: LogLevel,
    val message: String,
    val packageName: String,
    val tag: String,
    val timestampCreated: Instant,
    val throwable: Throwable?
) {
    companion object {
        /**
         * Provides a dummy instance for use in previews and tests.
         */
        fun mock(
            id: UUID = UUID.randomUUID(),
            level: LogLevel = LogLevel.DEBUG,
            message: String = "Hello World!",
            packageName: String = BuildConfig.LIBRARY_PACKAGE_NAME,
            tag: String = "LogEntity",
            timestampCreated: Instant = Instant.now(),
            throwable: Throwable? = null
        ): LogEntity =
            LogEntity(
                id = id,
                level = level,
                message = message,
                packageName = packageName,
                tag = tag,
                timestampCreated = timestampCreated,
                throwable = throwable
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
