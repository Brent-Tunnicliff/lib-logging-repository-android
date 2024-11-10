// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.room.TypeConverter
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.UUID

internal class Converters {
    //region Instant

    @TypeConverter
    fun fromInstant(value: Instant): Long = value.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long): Instant = Instant.ofEpochMilli(value)

    //endregion
    //region LogLevel

    @TypeConverter
    fun fromLogLevel(value: LogLevel): String = value.name

    @TypeConverter
    fun toLogLevel(value: String): LogLevel = LogLevel.valueOf(value)

    //endregion
    //region Throwable

    @TypeConverter
    fun fromThrowable(value: LogEntity.Throwable): String =
        Json.encodeToString(value)

    @TypeConverter
    fun toThrowable(value: String): LogEntity.Throwable =
        Json.decodeFromString(value)

    // endregion
    // region UUID

    @TypeConverter
    fun fromUUID(value: UUID): String = value.toString()

    @TypeConverter
    fun toUUID(value: String): UUID = UUID.fromString(value)

    // endregion
}
