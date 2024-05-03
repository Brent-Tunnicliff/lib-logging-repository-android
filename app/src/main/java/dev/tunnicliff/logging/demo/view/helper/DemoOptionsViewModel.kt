package dev.tunnicliff.logging.demo.view.helper

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.tunnicliff.logging.model.LogLevel
import kotlin.random.Random

class DemoOptionsViewModel : ViewModel() {
    private companion object {
        val CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        const val TAG = "DEMO_LOG"
    }

    fun sendLogsClicked(
        logLevel: LogLevel,
        includeThrowable: Boolean,
        numberToSend: Int
    ) {

    }

    private fun sendLog(
        logLevel: LogLevel,
        throwable: Throwable?
    ) {
        when (logLevel) {
            LogLevel.CRITICAL -> Log.wtf(TAG, getRandomString(), throwable)
            LogLevel.DEBUG -> Log.d(TAG, getRandomString(), throwable)
            LogLevel.ERROR -> Log.e(TAG, getRandomString(), throwable)
            LogLevel.INFO -> Log.i(TAG, getRandomString(), throwable)
            LogLevel.WARNING -> Log.w(TAG, getRandomString(), throwable)
        }
    }

    private fun getRandomString(): String {
        var result = ""
//        val numberOfWords = Random.nextInt(1, 100)
        val numberOfWords = 1
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
}