// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

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


    @Query("SELECT * FROM LogEntity ORDER BY timestampCreated DESC, id LIMIT :limit")
    suspend fun getFirstLogs(limit: Int): List<LogEntity>

    @Query("SELECT * FROM LogEntity WHERE id=:id")
    suspend fun getLog(id: UUID): LogEntity

    @Query("SELECT * FROM LogEntity WHERE (timestampCreated, id) < (:timestampCreated, :id) ORDER BY timestampCreated DESC, id LIMIT :limit")
    suspend fun getNextLogs(
        id: UUID,
        limit: Int,
        timestampCreated: Instant
    ): List<LogEntity>

    @Query("SELECT * FROM LogEntity WHERE (timestampCreated, id) > (:timestampCreated, :id) ORDER BY timestampCreated DESC, id LIMIT :limit")
    suspend fun getPreviousLogs(
        id: UUID,
        limit: Int,
        timestampCreated: Instant
    ): List<LogEntity>

    @Insert
    suspend fun insert(logEntity: LogEntity)

    @Query("SELECT count(*) FROM LogEntity")
    fun observeCount(): LiveData<Int>

    @Update
    suspend fun update(vararg logEntity: LogEntity): Int
}