// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging

import dev.tunnicliff.logging.logger.Logger

/**
 * Convenient access to the logger.
 *
 * If the LoggingContainer has not been initialised yet, then the thread is blocked until it is.
 */
val LOG: Logger
    get() = LoggingContainer.LOGGER
