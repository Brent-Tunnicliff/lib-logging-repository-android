package dev.tunnicliff.logging.internal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
internal interface LogDao {
    @Query("SELECT * FROM LogEntity where id=:id")
    fun getLog(id: Long): LogEntity

    @Insert
    suspend fun insert(logEntity: LogEntity): Long

    @Update
    suspend fun update(vararg logEntity: LogEntity): Int
}