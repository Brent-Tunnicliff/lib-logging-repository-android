// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger.internal

import org.junit.Assert.assertEquals
import org.junit.Test

class IntSizeFromBytesTests {
    @Test
    fun sizeFromBytesReturnsExpected() {
        assertEquals("Unexpected negative number: -1", (-1).sizeFromBytes())
        assertEquals("63 Bytes", 63.sizeFromBytes())
        assertEquals("1023 Bytes", 1023.sizeFromBytes())
        assertEquals("1 KB", 1024.sizeFromBytes())
        assertEquals("1 KB", 1030.sizeFromBytes())
        assertEquals("407 KB", 417_030.sizeFromBytes())
        assertEquals("1023 KB", 1_048_575.sizeFromBytes())
        assertEquals("1 MB", 1_048_576.sizeFromBytes())
        assertEquals("878 MB", 921_048_576.sizeFromBytes())
        assertEquals("1023 MB", 1_073_741_823.sizeFromBytes())
        assertEquals("1 GB", 1_073_741_824.sizeFromBytes())
    }
}