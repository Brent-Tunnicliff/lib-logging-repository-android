package dev.tunnicliff.logging.internal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.tunnicliff.logging.model.LogLevel
import java.time.Instant
import java.util.UUID

@Entity
internal data class LogEntity(
    // UUID for primary key is probably overly complicated for this use case,
    // but I wanted to do it for the learning experience.
    @PrimaryKey
    val id: UUID,
    val level: LogLevel,
    val message: String,
    val tag: String,
    val timestampCreated: Instant,
    var timestampUpdated: Instant,
    val throwable: Throwable?,
    var uploaded: Boolean
)
