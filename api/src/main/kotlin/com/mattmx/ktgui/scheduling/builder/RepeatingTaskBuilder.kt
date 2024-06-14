package com.mattmx.ktgui.scheduling.builder

import com.mattmx.ktgui.scheduling.IteratingTask
import com.mattmx.ktgui.scheduling.asyncRepeat
import com.mattmx.ktgui.scheduling.syncRepeat
import org.bukkit.scheduler.BukkitTask

class RepeatingTaskBuilder(
    val isAsync: Boolean
) {
    var max = -1L
        private set
    var delay = 0L
        private set
    var period = 0L
        private set
    lateinit var block: (IteratingTask) -> Unit
        private set

    infix fun repeat(times: Long) = apply {
        this.max = times
    }

    infix fun delay(ticks: Long) = apply {
        this.delay = ticks
    }

    infix fun period(ticks: Long) = apply {
        this.period = ticks
    }

    infix fun runs(block: IteratingTask.() -> Unit) = apply {
        this.block = block
    }

    fun run(): IteratingTask {
        var task: IteratingTask? = null

        val block: BukkitTask.() -> Unit = task@{
            if (task!!.iterations > max) {
                cancel()
                return@task
            }

            block.invoke(task!!)
            task!!.iterations++
        }

        task = IteratingTask(if (isAsync) asyncRepeat(period, delay, block) else syncRepeat(period, delay, block))

        return task
    }
}