package com.mattmx.ktgui.scheduling

import com.mattmx.ktgui.utils.Invokable
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * Utility class used as a "Task pool".
 *
 * Any scheduled tasks will be added to [list].
 */
class TaskTracker : Invokable<TaskTracker> {
    private val list = Collections.synchronizedList(arrayListOf<IteratingTask>())

    infix fun runAsync(block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, async {
            block(task!!)
            list.remove(task!!)
        }).apply { list.add(this) }
    }

    infix fun runSync(block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, sync {
            block(task!!)
            list.remove(task!!)
        }).apply { list.add(this) }
    }

    fun runAsyncLater(period: Long, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, asyncDelayed(period) {
            block(task!!)
            list.remove(task!!)
        }).apply { list.add(this) }
    }

    fun runSyncLater(period: Long, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, syncDelayed(period) {
            block(task!!)
            list.remove(task!!)
        }).apply { list.add(this) }
    }

    fun runAsyncRepeat(delay: Long, period: Long = 0L, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, asyncRepeat(delay, period) {
            block(task!!)
            task!!.iterations++
        }).apply { list.add(this) }
    }

    fun runSyncRepeat(delay: Long, period: Long = 0L, block: TaskTrackerTask.() -> Unit) {
        var task: TaskTrackerTask? = null
        task = TaskTrackerTask(this, syncRepeat(delay, period) {
            block(task!!)
            task!!.iterations++
        }).apply { list.add(this) }
    }

    fun cancelAll() = list.apply {
        val temp = this.toList()
        temp.forEach { it.cancel() }
        list.clear()
    }

    infix fun cancelIf(predicate: (IteratingTask) -> Boolean): List<IteratingTask> {
        val removed = arrayListOf<IteratingTask>()
        val it = list.iterator()
        while (it.hasNext()) {
            val t = it.next()
            if (predicate(t)) {
                it.remove()
                removed.add(t)
            }
        }
        return removed.toList()
    }

    inline fun <reified T : TaskTrackerTask> cancelIfInstance() =
        cancelIf { it is T } as List<T>

    infix fun removeTask(task: BukkitTask) = cancelIf { it.task == task }

    infix fun removeTask(task: TaskTrackerTask) {
        list.remove(task)
    }

}