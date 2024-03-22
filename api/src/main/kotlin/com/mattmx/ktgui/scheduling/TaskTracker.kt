package com.mattmx.ktgui.scheduling

import org.bukkit.scheduler.BukkitTask
import java.util.*

class TaskTracker<T> private constructor(
    private val owner: T
) {

    private val list = Collections.synchronizedList(arrayListOf<BukkitTask>())

    fun runAsync(block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, owner.async {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runSync(block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, owner.sync {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runAsyncLater(period: Long, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, owner.asyncDelayed(period) {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runSyncLater(period: Long, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, owner.syncDelayed(period) {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runAsyncRepeat(delay: Long, period: Long = 0L, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, owner.asyncRepeat(delay, period) {
            block(task!!)
            task!!.iterations++
        }.apply { list.add(this) })
    }

    fun runSyncRepeat(delay: Long, period: Long = 0L, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, owner.syncRepeat(delay, period) {
            block(task!!)
            task!!.iterations++
        }.apply { list.add(this) })
    }

    fun cancelAll() = list.apply {
        forEach { it.cancel() }
        list.clear()
    }

    fun removeTask(task: BukkitTask) {
        list.remove(task)
    }

    companion object {
        @JvmStatic
        fun <T> create(owner: T): TaskTracker<T> {
            return TaskTracker(owner)
        }
    }
}

fun <T> T.taskTracker() = TaskTracker.create(this)