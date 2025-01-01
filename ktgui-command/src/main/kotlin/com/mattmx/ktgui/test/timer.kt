package com.mattmx.ktgui.test

import java.time.Duration
import java.time.Instant

fun timeAndLog(
    id: String = "",
    nanos: Boolean = false,
    block: () -> Unit
) {
    val start = Instant.now()

    block()

    val timeTaken = Duration.between(start, Instant.now())

    if (nanos) {
        println("$id Taken ${timeTaken.toNanos()}ns")
    } else {
        println("$id Taken ${timeTaken.toMillis()}ms")
    }
}