package com.mattmx.ktgui.tasks

import kotlin.time.Duration
import java.util.*

data class TaskSpec<T>(
    val callback: (T) -> Unit,
    val async: Boolean = true,
    val period: Optional<Duration> = Optional.empty<Duration>(),
    val delay: Optional<Duration> = Optional.empty<Duration>(),
) {
    fun isRepeating() = period.isPresent
}