package com.mattmx.ktgui.scheduling

import org.bukkit.scheduler.BukkitTask
import java.util.*

class TaskTracker {
    private val list = Collections.synchronizedList(arrayListOf<BukkitTask>())

    fun runAsync(block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, async {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runSync(block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, sync {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runAsyncLater(period: Long, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, asyncDelayed(period) {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runSyncLater(period: Long, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, syncDelayed(period) {
            block(task!!)
            list.remove(this)
        }.apply { list.add(this) })
    }

    fun runAsyncRepeat(delay: Long, period: Long = 0L, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, asyncRepeat(delay, period) {
            block(task!!)
            task!!.iterations++
        }.apply { list.add(this) })
    }

    fun runSyncRepeat(delay: Long, period: Long = 0L, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, syncRepeat(delay, period) {
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

}