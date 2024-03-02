package dev.tunnicliff.logging.repository.internal.database

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
        private var INSTANCE: LoggingRepositoryDatabase? = null

        fun getDatabase(context: Context): LoggingRepositoryDatabase =
            INSTANCE?.let {
                return it
            } ?: run {
                val database = Room.databaseBuilder(
                    context,
                    LoggingRepositoryDatabase::class.java,
                    "logging-repository-database"
                ).build()

                INSTANCE = database
                return database
            }
    }

    abstract fun logDao(): LogDao
}