package com.example.todoappcompose

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private suspend fun myDelay() {
        println("myDelay: start...")
        delay(1000L)
        println("myDelay: end...")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addition_isCorrect() = runTest(UnconfinedTestDispatcher()) {
//            myDelay()
//        Dispatchers.setMain(UnconfinedTestDispatcher())
        launch {
            println("start child coroutine")
            delay(1000L)
            println("end child coroutine")
        }
//        this.advanceUntilIdle()
            println("assert code")
            assertEquals(4, 2 + 2)
        }

    @Test
    fun coroutine_test() {
        GlobalScope.launch {
            launch { println("launch 1") }
            launch { println("launch 2") }
            println("after launch 1 and launch 2")
        }
        println("after global")
    }
}