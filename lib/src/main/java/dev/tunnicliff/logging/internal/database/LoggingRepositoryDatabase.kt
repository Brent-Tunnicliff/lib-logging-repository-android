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
internal abstract class LoggingRepositoryDatabase : RoomDatabase() {
    companion object {
        fun new(context: Context): LoggingRepositoryDatabase =
            Room.databaseBuilder(
                context,
                LoggingRepositoryDatabase::class.java,
                "logging-repository-database"
            ).build()
    }

    abstract fun logDao(): LogDao
}