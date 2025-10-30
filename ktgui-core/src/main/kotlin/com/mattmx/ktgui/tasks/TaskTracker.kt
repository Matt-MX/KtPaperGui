package com.mattmx.ktgui.tasks

import com.mattmx.ktgui.TaskWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

abstract class TaskTracker<T : TaskWrapper> : TaskProvider<T>() {
    protected val tasks: MutableSet<T> = ConcurrentHashMap.newKeySet<T>()

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

    fun runAsync(callback: suspend (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = true))
    }

    fun runSyncDelayed(delay: Duration, callback: (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = false, delay = Optional.of(delay)))
    }

    fun runAsyncDelayed(delay: Duration, callback: suspend (T) -> Unit): T {
        return createTask(TaskSpec(callback, async = true, delay = Optional.of(delay)))
    }

    fun runSyncRepeat(period: Duration, delay: Duration = period, callback: (T) -> Unit): T {
        return createTask(
            TaskSpec(callback, async = false, period = Optional.of(period), delay = Optional.of(delay))
        )
    }

    fun runAsyncRepeat(period: Duration, delay: Duration = period, callback: suspend (T) -> Unit): T {
        return createTask(
            TaskSpec(callback, async = true, period = Optional.of(period), delay = Optional.of(delay))
        )
    }

    inline fun <reified K : T> cancelIfInstance() = cancelIfInstanceOf(K::class.java)

    fun <K : T> cancelIfInstanceOf(clazz: Class<K>) = cancelIf { clazz.isInstance(it) }

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