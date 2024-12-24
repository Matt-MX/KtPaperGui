package com.mattmx.ktgui.tasks

import com.mattmx.ktgui.TaskWrapper
import net.kyori.adventure.key.Key
import java.util.*
import kotlin.time.Duration

abstract class KeyedTaskTracker<T : TaskWrapper> : TaskProvider<T>() {
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

    fun runSyncDelayed(key: Key, delay: Duration, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) { createTask(TaskSpec(callback, async = false, delay = Optional.of(delay))) }
    }

    fun runAsyncDelayed(key: Key, delay: Duration, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) { createTask(TaskSpec(callback, async = true, delay = Optional.of(delay))) }
    }

    fun runSyncRepeat(key: Key, period: Duration, delay: Duration = period, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) {
            createTask(
                TaskSpec(callback, async = false, period = Optional.of(period), delay = Optional.of(delay))
            )
        }
    }

    fun runAsyncRepeat(key: Key, period: Duration, delay: Duration = period, callback: (T) -> Unit): T {
        return tasks.getOrPut(key) {
            createTask(
                TaskSpec(callback, async = true, period = Optional.of(period), delay = Optional.of(delay))
            )
        }
    }

    inline fun <reified K : T> cancelIfInstance() = cancelIfInstanceOf(K::class.java)

    fun <K : T> cancelIfInstanceOf(clazz: Class<K>) = cancelIf { _, v -> clazz.isInstance(v) }

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