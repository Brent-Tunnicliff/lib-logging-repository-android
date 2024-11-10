// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.test.helper

import java.io.IOException

class MockException(
    message: String? = "Mock error",
    cause: Throwable? = null
) : kotlin.Exception(message, cause)

class MockIOException(
    message: String? = "Mock error",
    cause: Throwable? = null
) : IOException(message, cause)