// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.logger

interface Logger {
    fun debug(tag: String, message: String, throwable: Throwable? = null)
    fun info(tag: String, message: String, throwable: Throwable? = null)
    fun warning(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
    fun critical(tag: String, message: String, throwable: Throwable? = null)

    /**
     * Used for extending to add singleton logger instances for modules and apps.
     */
    companion object {
        /**
         * Logger that does not perform any operation.
         *
         * Used as a default instance until a real one is set.
         */
        val NoOpLogger = object : Logger {
            override fun debug(tag: String, message: String, throwable: Throwable?) {}
            override fun info(tag: String, message: String, throwable: Throwable?) {}
            override fun warning(tag: String, message: String, throwable: Throwable?) {}
            override fun error(tag: String, message: String, throwable: Throwable?) {}
            override fun critical(tag: String, message: String, throwable: Throwable?) {}
        }
    }
}
