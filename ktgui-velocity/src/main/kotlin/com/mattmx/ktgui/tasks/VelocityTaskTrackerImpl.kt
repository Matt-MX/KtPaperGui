package com.mattmx.ktgui.tasks

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.scheduler.ScheduledTask
import kotlin.time.Duration

class VelocityTaskTrackerImpl(
    plugin: Any,
    proxyServer: ProxyServer
) : TaskTracker<ScheduledTask, Duration>() {
    private val provider = VelocityTaskProvider(plugin, proxyServer) { completedTask ->
        tasks.remove(completedTask)
    }

    override fun cancel(task: ScheduledTask) {
        task.cancel()
    }

    override fun createTask(spec: TaskSpec<ScheduledTask, Duration>): ScheduledTask {
        return provider.createTask(spec)
    }

}