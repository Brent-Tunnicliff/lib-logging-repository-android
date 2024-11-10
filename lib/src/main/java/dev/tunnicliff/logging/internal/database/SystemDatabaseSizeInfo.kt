// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import androidx.room.ColumnInfo

internal data class SystemDatabaseSizeInfo(
    @ColumnInfo("page_size")
    val pageSizeBytes: Int? = null,
    @ColumnInfo("page_count")
    val pageCount: Int? = null
) {
    fun calculateSize(): Int =
        if (pageSizeBytes == null || pageCount == null) {
            0
        } else {
            pageSizeBytes * pageCount
        }
}
