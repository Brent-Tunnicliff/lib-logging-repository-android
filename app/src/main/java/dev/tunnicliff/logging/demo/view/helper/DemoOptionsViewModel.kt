// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tunnicliff.logging.demo.Log
import dev.tunnicliff.logging.model.LogLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

abstract class DemoOptionsViewModel : ViewModel() {
    abstract val logLevel: StateFlow<LogLevel>
    abstract val includeThrowable: StateFlow<Boolean>
    abstract val sendRandomLogs: StateFlow<Boolean>
    abstract fun sendLogsClicked()
    abstract fun updateIncludeThrowable(value: Boolean)
    abstract fun updateLogLevel(value: LogLevel)
    abstract fun updateSendRandomLogs(value: Boolean)
}

class DefaultDemoOptionsViewModel : DemoOptionsViewModel() {
    private companion object {
        val CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        const val TAG = "DEMO_LOG"
    }

    private val _logLevel = MutableStateFlow(LogLevel.DEBUG)
    override val logLevel: StateFlow<LogLevel>
        get() = _logLevel

    private val _includeThrowable = MutableStateFlow(false)
    override val includeThrowable: StateFlow<Boolean>
        get() = _includeThrowable

    private val _sendRandomLogs = MutableStateFlow(false)
    override val sendRandomLogs: StateFlow<Boolean>
        get() = _sendRandomLogs

    init {
        viewModelScope.launch {
            while (true) {
                if (sendRandomLogs.value) {
                    sendLog(
                        LogLevel.entries.random(),
                        listOf(true, false).random()
                    )
                }

                delay(1000)
            }
        }
    }

    // region DemoOptionsViewModel

    override fun sendLogsClicked() {
        sendLog(
            logLevel.value,
            includeThrowable.value
        )
    }

    override fun updateIncludeThrowable(value: Boolean) {
        viewModelScope.launch {
            _includeThrowable.emit(value)
        }
    }

    override fun updateLogLevel(value: LogLevel) {
        viewModelScope.launch {
            _logLevel.emit(value)
        }
    }

    override fun updateSendRandomLogs(value: Boolean) {
        viewModelScope.launch {
            _sendRandomLogs.emit(value)
        }
    }

    // endregion

    // region Private functions

    private fun sendLog(
        logLevel: LogLevel,
        includeThrowable: Boolean
    ) {
        val throwable = if (includeThrowable) Exception(getRandomString()) else null
        when (logLevel) {
            LogLevel.CRITICAL -> Log.critical(TAG, getRandomString(), throwable)
            LogLevel.DEBUG -> Log.debug(TAG, getRandomString(), throwable)
            LogLevel.ERROR -> Log.error(TAG, getRandomString(), throwable)
            LogLevel.INFO -> Log.info(TAG, getRandomString(), throwable)
            LogLevel.WARNING -> Log.warning(TAG, getRandomString(), throwable)
        }
    }

    private fun getRandomString(): String {
        var result = ""
        val numberOfWords = Random.nextInt(1, 100)
        for (word in 1..numberOfWords) {
            val numberOfCharacters = Random.nextInt(1, 100)

            for (character in 1..numberOfCharacters) {
                result += Random.nextInt(0, CHAR_POOL.size).let {
                    CHAR_POOL[it]
                }
            }
        }

        return result
    }

    // endregion
}

class PreviewDemoOptionsViewModel(
    logLevel: LogLevel,
    includeThrowable: Boolean,
    sendRandomLogs: Boolean
) : DemoOptionsViewModel() {
    override val logLevel = MutableStateFlow(logLevel)
    override val includeThrowable = MutableStateFlow(includeThrowable)
    override val sendRandomLogs = MutableStateFlow(sendRandomLogs)

    override fun sendLogsClicked() {}
    override fun updateIncludeThrowable(value: Boolean) {}
    override fun updateLogLevel(value: LogLevel) {}
    override fun updateSendRandomLogs(value: Boolean) {}
}