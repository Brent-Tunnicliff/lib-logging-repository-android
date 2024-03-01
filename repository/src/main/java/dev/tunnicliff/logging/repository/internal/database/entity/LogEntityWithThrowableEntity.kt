package dev.tunnicliff.logging.repository.internal.database.entity

import androidx.room.Embedded
import androidx.room.Relation

internal data class LogEntityWithThrowableEntity(
    @Embedded
    val logEntity: LogEntity,
    @Relation(
        parentColumn = "throwableId",
        entityColumn = "id"
    )
    val cause: ThrowableEntityWithCause?
)
