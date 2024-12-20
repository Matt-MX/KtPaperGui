package com.mattmx.ktgui.tasks

import java.util.*

data class TaskSpec<T, D : Any>(
    val callback: (T) -> Unit,
    val async: Boolean,
    val period: Optional<D> = Optional.empty<D>(),
    val delay: Optional<D> = Optional.empty<D>(),
) {
    fun isRepeating() = period.isPresent
}