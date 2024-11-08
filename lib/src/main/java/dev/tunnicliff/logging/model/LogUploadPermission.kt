// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.model

/**
 * Represents if the user has allowed uploading logs to an external service.
 */
enum class LogUploadPermission {
    ALLOWED,
    NOT_SET,
    REJECTED;

    val isAllowed: Boolean
        get() = when (this) {
            ALLOWED -> true
            NOT_SET,
            REJECTED -> false
        }
}