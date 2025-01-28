// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

fun Int.sizeFromBytes(): String =
    when {
        this < 0 -> "Unexpected negative number: $this"
        this < IntConstants.BYTES_IN_KILOBYTE -> "$this Bytes"
        this < IntConstants.KILOBYTES_IN_MEGABYTE -> "${this / IntConstants.BYTES_IN_KILOBYTE} KB"
        this < IntConstants.MEGABYTES_IN_GIGABYTE -> "${this / IntConstants.KILOBYTES_IN_MEGABYTE} MB"
        else -> "${this / IntConstants.MEGABYTES_IN_GIGABYTE} GB"
    }

private object IntConstants {
    const val BYTES_IN_KILOBYTE = 1024
    const val KILOBYTES_IN_MEGABYTE = 1024 * BYTES_IN_KILOBYTE
    const val MEGABYTES_IN_GIGABYTE = 1024 * KILOBYTES_IN_MEGABYTE
}
