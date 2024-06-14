package com.mattmx.ktgui.scheduling.builder

import com.mattmx.ktgui.scheduling.asyncDelayed
import com.mattmx.ktgui.scheduling.asyncRepeat
import com.mattmx.ktgui.utils.seconds

object Task {

    @JvmStatic
    fun async() = AsyncTaskBuilder()

    @JvmStatic
    fun sync() = SyncTaskBuilder()
}

fun main() {
    val repeating = Task
        .async()
        .repeating()
        .repeat(20)
        .delay(2.seconds)
        .period(1)
        .runs {
            println("Task repeating $iterations")
        }.run()

    val later = Task
        .sync()
        .later()
        .delay(1)
        .runs {
            println(iterations)
        }.run()

    val dsl = Task.async() repeating {
    } period 2 delay 2 repeat 20
}