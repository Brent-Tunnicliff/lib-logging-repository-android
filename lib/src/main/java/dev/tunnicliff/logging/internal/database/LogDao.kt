// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.UUID

@Dao
internal interface LogDao {
    @Query("SELECT * FROM LogEntity WHERE id=:id")
    suspend fun getLog(id: UUID): LogEntity

    @Query("SELECT * FROM LogEntity ORDER BY timestampCreated DESC, id")
    fun getLogs(): PagingSource<Int, LogEntity>

    @Insert
    suspend fun insert(logEntity: LogEntity)

    @Update
    suspend fun update(vararg logEntity: LogEntity): Int
}