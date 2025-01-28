// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.Instant

@Dao
internal interface LogDao {
    @Query("DELETE from LogEntity where timestampCreated<=:timestamp")
    suspend fun deleteLogsOlderThan(timestamp: Instant): Int

    @Query("SELECT * FROM LogEntity ORDER BY timestampCreated DESC, id")
    fun getLogs(): PagingSource<Int, LogEntity>

    @Insert
    suspend fun insert(logEntity: LogEntity)

    // Found here: https://stackoverflow.com/a/61998905
    @Suppress("AndroidUnresolvedRoomSqlReference")
    @Query("SELECT s.*, c.* FROM pragma_page_size as s JOIN pragma_page_count as c")
    suspend fun getDatabaseSizeInfo(): List<SystemDatabaseSizeInfo>
}
