// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [LogEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
internal abstract class LoggingDatabase : RoomDatabase() {
    companion object {
        fun new(context: Context): LoggingDatabase =
            Room.databaseBuilder(
                context,
                LoggingDatabase::class.java,
                "lib-logging-database"
            ).build()
    }

    abstract fun logDao(): LogDao
}
