package dev.tunnicliff.logging.repository.internal.database.entity

import androidx.room.Embedded
import androidx.room.Relation

internal data class ThrowableEntityWithCause(
    @Embedded
    val throwableEntity: ThrowableEntity,
    @Relation(
        parentColumn = "causeId",
        entityColumn = "id"
    )
    val cause: ThrowableEntityWithCause?
)
