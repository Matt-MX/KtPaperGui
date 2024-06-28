package com.mattmx.ktgui.scheduling.builder

import com.mattmx.ktgui.scheduling.*
import org.bukkit.scheduler.BukkitTask

class LaterTaskBuilder(
    val isAsync: Boolean
) {
    var delay = 0L
        private set
    lateinit var block: (IteratingTask) -> Unit
        private set

    infix fun delay(ticks: Long) = apply {
        this.delay = ticks
    }


    infix fun runs(block: IteratingTask.() -> Unit) = apply {
        this.block = block
    }

    fun run(): IteratingTask {
        var task: IteratingTask? = null

        val block: BukkitTask.() -> Unit = task@{
            block.invoke(task!!)
            task!!.iterations++
        }

        task = IteratingTask(if (isAsync) asyncDelayed(delay, block) else syncDelayed(delay, block))

        return task
    }

}