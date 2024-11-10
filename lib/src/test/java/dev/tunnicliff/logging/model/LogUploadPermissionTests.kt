// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogUploadPermissionTests {
    @Test
    fun isAllowed() {
        assertTrue(LogUploadPermission.ALLOWED.isAllowed)
    }

    @Test
    fun isNotAllowed() {
        for (value in LogUploadPermission.entries.filter { it != LogUploadPermission.ALLOWED }) {
            assertFalse(value.name, value.isAllowed)
        }
    }
}