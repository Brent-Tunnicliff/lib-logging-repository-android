// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.internal.database

import android.net.http.HttpException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LogEntityTests {
    @Test
    fun toStringReturnsExpected() {
        val expectedType = "This is the type"
        val expectedMessage = "This is the message"
        val expectedResult = "$expectedType: $expectedMessage"
        val throwable = LogEntity.Throwable(
            type = expectedType,
            message = expectedMessage,
            // Cause is not expected to show up in the result.
            cause = LogEntity.Throwable.mock()
        )

        assertEquals(expectedResult, throwable.toString())
    }

    @Test
    fun toEntityReturnsExpected() {
        // The unit tests behave differently than the app running, so cannot get testing `cause` to work.
        val httpException = HttpException("Mock error", null)

        val expectedResult = LogEntity.Throwable(
            type = httpException::class.qualifiedName!!,
            message = httpException.message,
            cause = null
        )
        val result = httpException.toEntity()

        assertEquals(expectedResult.type, result.type)
        assertEquals(expectedResult.message, result.message)
        assertNull(result.cause?.message)
    }

    @Test
    fun flatCausesReturnsExpected() {
        val rootCause = LogEntity.Throwable.mock(type = "RootCause")
        val nextCause = LogEntity.Throwable.mock(type = "NextCause", cause = rootCause)
        val anotherCause = LogEntity.Throwable.mock(type = "AnotherCause", cause = nextCause)
        val finalThrowable = LogEntity.Throwable.mock(type = "FinalThrowable", cause = anotherCause)
        val expectedResult = listOf(
            anotherCause,
            nextCause,
            rootCause
        )
        val result = finalThrowable.flatCauses()
        assertEquals(expectedResult, result)
    }
}