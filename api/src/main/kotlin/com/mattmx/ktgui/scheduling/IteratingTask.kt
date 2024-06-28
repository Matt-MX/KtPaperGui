package com.mattmx.ktgui.scheduling

import org.bukkit.scheduler.BukkitTask

open class IteratingTask(
    val task: BukkitTask
) {
    /**
     * How many times the task has repeated.
     * Do not increment yourself.
     */
    var iterations = 0

    open fun cancel() = task.cancel()
}