package dev.tunnicliff.logging.demo

import dev.tunnicliff.logging.logger.Logger

/**
 * Convenient access to logging without having to use dependency injection.
 */
val Log: Logger
    get() = MainApplication.LOGGER