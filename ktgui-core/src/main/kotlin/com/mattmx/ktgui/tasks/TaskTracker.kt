package com.mattmx.ktgui.tasks

import java.util.*

abstract class TaskTracker<T, D : Any> : TaskProvider<T, D>() {
    protected val tasks = Collections.synchronizedSet(mutableSetOf<T>())

    fun track(task: T) = tasks.add(task)

    fun untrackSilently(task: T) = tasks.remove(task)

    operator fun contains(task: T) = tasks.contains(task)

    operator fun plusAssign(task: T) {
        track(task)
    }

    operator fun minusAssign(task: T) {
        cancelIf { it == task }
    }

    fun runSync(callback: (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = false))
    }

    fun runAsync(callback: (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = true))
    }

    fun runSyncDelayed(delay: D, callback: (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = false, delay = Optional.of(delay)))
    }

    fun runAsyncDelayed(delay: D, callback: (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = true, delay = Optional.of(delay)))
    }

    fun runSyncRepeat(period: D, delay: D = period, callback: (T) -> Unit): T {
        return createTask(
            TaskSpec(callback, async = false, period = Optional.of(period), delay = Optional.of(delay))
        )
    }

    fun runAsyncRepeat(period: D, delay: D = period, callback: (T) -> Unit): T {
        return createTask(
            TaskSpec(callback, async = true, period = Optional.of(period), delay = Optional.of(delay))
        )
    }

    fun cancelIf(predicate: (T) -> Boolean): List<T> = synchronized(tasks) {
        val removed = mutableListOf<T>()

        val it = tasks.iterator()
        while (it.hasNext()) {
            val task = it.next()

            if (predicate(task)) {
                cancel(task)
                it.remove()
                removed.add(task)
            }
        }

        return removed
    }

    fun cancelAll() = cancelIf { true }

    abstract fun cancel(task: T)

}