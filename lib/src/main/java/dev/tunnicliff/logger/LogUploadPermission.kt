package dev.tunnicliff.logger

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
