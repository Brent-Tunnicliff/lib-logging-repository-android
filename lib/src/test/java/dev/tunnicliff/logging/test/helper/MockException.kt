// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.test.helper

class MockException(
    message: String? = "Mock error",
    cause: Throwable? = null
) : kotlin.Exception(message, cause)