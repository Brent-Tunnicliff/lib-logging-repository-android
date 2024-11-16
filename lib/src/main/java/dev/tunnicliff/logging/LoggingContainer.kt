// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dev.tunnicliff.container.Container
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import dev.tunnicliff.logging.logger.LogUploadHandler
import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.logger.internal.DefaultLogUploader
import dev.tunnicliff.logging.logger.internal.DefaultLogWriter
import dev.tunnicliff.logging.logger.internal.DefaultLogger
import dev.tunnicliff.logging.logger.internal.DefaultLoggingConfigurationManager
import dev.tunnicliff.logging.logger.internal.LogUploader
import dev.tunnicliff.logging.logger.internal.LogWriter
import dev.tunnicliff.logging.logger.internal.SystemLog
import dev.tunnicliff.logging.logger.internal.SystemLogWrapper
import dev.tunnicliff.logging.view.internal.DefaultLogsViewModel
import dev.tunnicliff.logging.view.internal.LogsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.reflect.KClass

/**
 * Dependency injection container for the library.
 */
class LoggingContainer(
    private val dependencies: Dependencies
) : Container() {
    interface Dependencies {
        fun applicationContext(): Context
        fun uploadHandler(): LogUploadHandler
    }

    object ViewModelFactory : ViewModelProvider.Factory {
        private val resolver: LoggingContainer
            get() = SHARED!!

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
            when (modelClass) {
                LogsViewModel::class -> DefaultLogsViewModel(resolver.loggingPager()) as T
                else -> throw Exception("Unable to resolve view model of type $modelClass")
            }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val MAX_SIZE = 40

        private var SHARED: LoggingContainer? = null

        internal val LOGGER: Logger?
            get() = SHARED?.logger()
    }

    init {
        SHARED = this
    }

    // region Public

    fun logger(): Logger = resolveSingleton {
        val logger = DefaultLogger(
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            loggingConfigurationManager = loggingConfigurationManager(),
            logUploader = logUploader(),
            logWriter = logWriter(),
            systemLog = systemLog()
        )
        logger.info(tag = DefaultLogger.TAG, message = "Logger initialised")
        logger
    }

    fun loggingConfigurationManager(): LoggingConfigurationManager = resolveSingleton {
        DefaultLoggingConfigurationManager(
            context = dependencies.applicationContext(),
            logger = { logger() },
            database = loggingDatabase()
        )
    }

    // endregion

    // region Private

    internal fun loggingPager(): Pager<Int, LogEntity> =
        Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                maxSize = MAX_SIZE
            )
        ) {
            loggingDatabase().logDao().getLogs()
        }

    private fun loggingDatabase(): LoggingDatabase = resolveSingleton {
        LoggingDatabase.new(dependencies.applicationContext())
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
            database = loggingDatabase(),
            logUploader = { logUploader() },
            systemLog = systemLog(),
        )
    }

    private fun systemLog(): SystemLog = resolveWeak {
        SystemLogWrapper()
    }

    // endregion
}
