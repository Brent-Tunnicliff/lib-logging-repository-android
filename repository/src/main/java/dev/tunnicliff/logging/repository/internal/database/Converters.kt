package dev.tunnicliff.logging.repository.internal.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import dev.tunnicliff.logging.repository.LogLevel
import java.time.Instant

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
    fun fromThrowable(value: Throwable): String = Gson().toJson(value)

    @TypeConverter
    fun toThrowable(value: String): Throwable = Gson().fromJson(value, Throwable::class.java)
    //endregion
}