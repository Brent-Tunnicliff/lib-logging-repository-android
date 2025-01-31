// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dev.tunnicliff.container.Container
import dev.tunnicliff.logging.internal.LOG
import dev.tunnicliff.logging.internal.database.LogEntity
import dev.tunnicliff.logging.internal.database.LoggingDatabase
import dev.tunnicliff.logging.logger.Logger
import dev.tunnicliff.logging.logger.LoggingConfigurationManager
import dev.tunnicliff.logging.logger.internal.DefaultLogWriter
import dev.tunnicliff.logging.logger.internal.DefaultLogger
import dev.tunnicliff.logging.logger.internal.DefaultLoggingConfigurationManager
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
    private val applicationContext: Context
) : Container() {
    companion object {
        private const val PAGE_SIZE = 10
        private const val MAX_SIZE = 40

        private lateinit var RESOLVER: LoggingContainer

        internal val VIEW_MODEL_FACTORY = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                when (modelClass) {
                    LogsViewModel::class -> DefaultLogsViewModel(RESOLVER.loggingPager()) as T
                    else -> throw Exception("Unable to resolve view model of type $modelClass")
                }
        }
    }

    init {
        // We assume there is only ever one container initialised in the application.
        // But even if not it is probably ok to override.
        LOG = logger(BuildConfig.LIBRARY_PACKAGE_NAME)
        RESOLVER = this
    }

    // region Public

    fun logger(packageName: String): Logger = resolveSingleton(packageName) {
        val logger = DefaultLogger(
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            loggingConfigurationManager = loggingConfigurationManager(),
            logWriter = logWriter(),
            packageName = packageName,
            systemLog = systemLog()
        )
        logger.info(tag = DefaultLogger.TAG, message = "Logger initialised")
        logger
    }

    fun loggingConfigurationManager(): LoggingConfigurationManager = resolveSingleton {
        DefaultLoggingConfigurationManager(
            context = applicationContext,
            database = loggingDatabase()
        )
    }

    // endregion

    // region Private

    private fun loggingDatabase(): LoggingDatabase = resolveSingleton {
        LoggingDatabase.new(applicationContext)
    }

    private fun loggingPager(): Pager<Int, LogEntity> =
        Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                maxSize = MAX_SIZE
            )
        ) {
            loggingDatabase().logDao().getLogs()
        }

    private fun logWriter(): LogWriter = resolveWeak {
        DefaultLogWriter(
            database = loggingDatabase(),
            systemLog = systemLog(),
        )
    }

    private fun systemLog(): SystemLog = resolveWeak {
        SystemLogWrapper()
    }

    // endregion
}
