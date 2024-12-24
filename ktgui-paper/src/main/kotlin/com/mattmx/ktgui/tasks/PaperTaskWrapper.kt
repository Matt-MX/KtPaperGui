package com.mattmx.ktgui.tasks

import com.mattmx.ktgui.TaskWrapper
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

class PaperTaskWrapper(
    private val spec: TaskSpec<PaperTaskWrapper>,
    private val instance: ScheduledTask
) : TaskWrapper {
    override fun getSpec(): TaskSpec<Any> {
        return spec as TaskSpec<Any>
    }

    override fun cancel() {
        instance.cancel()
    }
}