package dev.tunnicliff.logging.repository.internal.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import dev.tunnicliff.logging.repository.LogLevel
import java.time.Instant

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ThrowableEntity::class,
            parentColumns = ["throwableId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
internal data class LogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val level: LogLevel,
    val message: String,
    val tag: String,
    val timestampCreated: Instant,
    var timestampUpdated: Instant,
    @Relation(parentColumn = "id", entityColumn = "logId")
    val throwableId: Long?,
    var uploaded: Boolean
)
