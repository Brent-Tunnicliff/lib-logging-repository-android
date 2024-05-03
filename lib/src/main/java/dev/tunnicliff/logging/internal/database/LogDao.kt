package dev.tunnicliff.logging.internal.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.time.Instant
import java.util.UUID

@Dao
internal interface LogDao {
    @Insert
    suspend fun insert(logEntity: LogEntity)

    @Query("SELECT * FROM LogEntity WHERE (timestampCreated, id) < (:lastTimestampCreated, :lastID) ORDER BY timestampCreated DESC, id LIMIT :limit")
    fun getLogsOrderedByNewestFirst(
        lastTimestampCreated: Instant,
        lastID: UUID,
        limit: Int
    ): List<LogEntity>

    @Query("SELECT * FROM LogEntity WHERE id=:id")
    fun getLog(id: UUID): LogEntity

    @Query("SELECT count(*) FROM LogEntity")
    fun observeCount(): LiveData<Int>

    @Update
    suspend fun update(vararg logEntity: LogEntity): Int
}