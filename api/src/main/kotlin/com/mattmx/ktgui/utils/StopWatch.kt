package com.mattmx.ktgui.utils

import java.time.Duration

class StopWatch(
    private val name: String = "sw"
) {
    private var timeStart = 0L

    fun start() = apply {
        timeStart = System.nanoTime()
    }

    fun elapsed() = System.nanoTime() - timeStart

    fun debug() {
        val nanos = elapsed()
        println(
            "  [$name] took ${nanos}ns (${nanos / 1_000_000}ms${
                if (nanos > 100_000_000) ", " + Duration.ofNanos(
                    nanos
                ).pretty() else ""
            })"
        )
    }
}