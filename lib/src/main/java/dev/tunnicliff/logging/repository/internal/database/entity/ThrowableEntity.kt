package dev.tunnicliff.logging.repository.internal.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ThrowableEntity::class,
            parentColumns = ["causeId"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
internal data class ThrowableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val message: String?,
    val causeId: Long?
)
