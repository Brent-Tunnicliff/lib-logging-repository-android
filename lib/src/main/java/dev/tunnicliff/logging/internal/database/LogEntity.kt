package dev.tunnicliff.logging.internal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.tunnicliff.logging.model.LogLevel
import java.time.Instant

@Entity
internal data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val level: LogLevel,
    val message: String,
    val tag: String,
    val timestampCreated: Instant,
    var timestampUpdated: Instant,
    val throwable: Throwable?,
    var uploaded: Boolean
)
