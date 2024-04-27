package dev.tunnicliff.logging.logger

import android.content.Context
import dev.tunnicliff.container.Container
import dev.tunnicliff.logging.internal.DefaultLogUploader
import dev.tunnicliff.logging.internal.DefaultLogWriter
import dev.tunnicliff.logging.internal.DefaultLogger
import dev.tunnicliff.logging.internal.DefaultLoggingConfigurationManager
import dev.tunnicliff.logging.internal.LogUploader
import dev.tunnicliff.logging.internal.LogWriter
import dev.tunnicliff.logging.internal.SystemLog
import dev.tunnicliff.logging.internal.SystemLogWrapper
import dev.tunnicliff.logging.internal.database.LoggingRepositoryDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

/**
 * Dependency injection container for the library.
 */
class LoggingContainer(
    private val dependencies: Dependencies
) : Container() {
    interface Dependencies {
        fun applicationContext(): Context
        fun uploadHandler(): LogUploadHandler?
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun logger(): Logger = resolveSingleton {
        DefaultLogger(
            // Using GlobalScope is not ideal, but wanted to avoid having to make each log function `suspend`.
            // Probably not an issue though since ideally LoggingRepository's lifecycle should match the app.
            coroutineScope = GlobalScope,
            loggingConfigurationManager = loggingConfigurationManager(),
            logUploader = logUploader(),
            logWriter = logWriter(),
            systemLog = systemLog()
        )
    }

    fun loggingConfigurationManager(): LoggingConfigurationManager = resolveSingleton {
        DefaultLoggingConfigurationManager(
            context = dependencies.applicationContext(),
            logger = { logger() }
        )
    }

    private fun loggingRepositoryDatabase(): LoggingRepositoryDatabase = resolveSingleton {
        LoggingRepositoryDatabase.new(dependencies.applicationContext())
    }

    private fun logUploader(): LogUploader = resolveWeak {
        DefaultLogUploader(
            loggingConfigurationManager = loggingConfigurationManager(),
            logWriter = { logWriter() },
            systemLog = systemLog(),
            uploadHandler = dependencies.uploadHandler()
        )
    }

    private fun logWriter(): LogWriter = resolveWeak {
        DefaultLogWriter(
            database = loggingRepositoryDatabase(),
            logUploader = { logUploader() },
            systemLog = systemLog(),
        )
    }

    private fun systemLog(): SystemLog = resolveWeak {
        SystemLogWrapper()
    }
}
