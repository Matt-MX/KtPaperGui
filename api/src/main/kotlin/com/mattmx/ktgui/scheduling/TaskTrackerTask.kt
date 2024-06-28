package com.mattmx.ktgui.scheduling

import org.bukkit.scheduler.BukkitTask

/**
 * Wrapper class for the [BukkitTask] task.
 */
open class TaskTrackerTask(
    private val owner: TaskTracker,
    task: BukkitTask
) : IteratingTask(task) {

    override fun cancel() {
        owner.removeTask(task)
        task.cancel()
    }
}