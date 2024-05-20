package com.mattmx.ktgui.scheduling

import org.bukkit.scheduler.BukkitTask

/**
 * Wrapper class for the [BukkitTask] task.
 */
class TaskTrackerTask(
    private val owner: TaskTracker,
    private val task: BukkitTask
) {
    /**
     * How many times the task has repeated.
     * Do not increment yourself.
     */
    var iterations = 0

    fun cancel() {
        owner.removeTask(task)
        task.cancel()
    }
}