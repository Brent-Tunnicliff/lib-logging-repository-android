package dev.tunnicliff.logging.repository.internal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.tunnicliff.logging.repository.internal.database.entity.LogEntity
import dev.tunnicliff.logging.repository.internal.database.entity.LogEntityWithThrowableEntity
import dev.tunnicliff.logging.repository.internal.database.entity.ThrowableEntity

@Dao
internal interface LogDao {
    @Transaction
    @Query("SELECT * FROM LogEntity")
    fun getAll(): List<LogEntityWithThrowableEntity>

    @Insert
    fun insert(logEntity: LogEntity): Long

    @Insert
    fun insert(throwableEntity: ThrowableEntity): Long

    @Update
    fun update(vararg logEntity: LogEntity): Long
}