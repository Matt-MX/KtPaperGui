package com.mattmx.ktgui.tasks

import com.mattmx.ktgui.TaskWrapper
import com.velocitypowered.api.scheduler.ScheduledTask

class VelocityTaskWrapper(
    private val spec: TaskSpec<VelocityTaskWrapper>
) : TaskWrapper {
    lateinit var instance: ScheduledTask

    override fun getSpec(): TaskSpec<Any> {
        return this.spec as TaskSpec<Any>
    }

    override fun cancel() {
        instance.cancel()
    }
}