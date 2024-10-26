// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

package dev.tunnicliff.logging.demo.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tunnicliff.logging.demo.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

abstract class MainViewModel : ViewModel()

class DefaultMainViewModel : MainViewModel() {
    private companion object {
        val CHAR_POOL: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        const val TAG = "MainViewModel"
    }

    init {
        createLogEverySecond()
    }

    private fun createLogEverySecond() {
        viewModelScope.launch {
            while (true) {
                Log.info(
                    tag = TAG,
                    message = getRandomString()
                )
                delay(1000)
            }
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
