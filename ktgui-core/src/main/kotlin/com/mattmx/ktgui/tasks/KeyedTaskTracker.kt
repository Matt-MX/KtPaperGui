package com.mattmx.ktgui.tasks

import net.kyori.adventure.key.Key
import java.util.*

abstract class KeyedTaskTracker<T, D : Any> : TaskProvider<T, D>() {
    protected val tasks = Collections.synchronizedMap(hashMapOf<Key, T>())

    fun track(key: Key, task: T) = tasks.put(key, task)

    operator fun contains(key: Key) = tasks.containsKey(key)

    fun cancel(key: Key) = tasks.remove(key)?.also { cancel(it) }

    fun runSync(key: Key, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) { createTask(TaskSpec(callback, async = false)) }
    }

    fun runAsync(key: Key, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) { createTask(TaskSpec(callback, async = true)) }
    }

    fun runSyncDelayed(key: Key, delay: D, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) { createTask(TaskSpec(callback, async = false, delay = Optional.of(delay))) }
    }

    fun runAsyncDelayed(key: Key, delay: D, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) { createTask(TaskSpec(callback, async = true, delay = Optional.of(delay))) }
    }

    fun runSyncRepeat(key: Key, period: D, delay: D = period, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) {
            createTask(
                TaskSpec(callback, async = false, period = Optional.of(period), delay = Optional.of(delay))
            )
        }
    }

    fun runAsyncRepeat(key: Key, period: D, delay: D = period, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) {
            createTask(
                TaskSpec(callback, async = true, period = Optional.of(period), delay = Optional.of(delay))
            )
        }
    }

    fun cancelIf(predicate: (Key, T) -> Boolean): List<T> = synchronized(tasks) {
        val removed = mutableListOf<T>()

        val it = tasks.iterator()
        while (it.hasNext()) {
            val (key, task) = it.next()

            if (predicate(key, task)) {
                cancel(task)
                it.remove()
                removed.add(task)
            }
        }

        return removed
    }

    fun cancelAll() = cancelIf { _, _ -> true }

    abstract fun cancel(task: T)
}