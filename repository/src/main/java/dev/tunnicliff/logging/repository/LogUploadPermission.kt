package dev.tunnicliff.logging.repository

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