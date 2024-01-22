package com.mattmx.ktgui.scheduling

import org.bukkit.scheduler.BukkitTask

class TaskTrackerTask(
    private val owner: TaskTracker,
    private val task: BukkitTask
) {
    var iterations = 0

    fun cancel() {
        owner.removeTask(task)
        task.cancel()
    }
}