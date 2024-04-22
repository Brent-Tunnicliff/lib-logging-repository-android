package dev.tunnicliff.logging.repository.internal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
internal interface LogDao {
    @Transaction
    @Query("SELECT * FROM LogEntity")
    fun getAll(): List<LogEntity>

    @Insert
    suspend fun insert(logEntity: LogEntity): Long

    @Update
    suspend fun update(vararg logEntity: LogEntity): Int
}