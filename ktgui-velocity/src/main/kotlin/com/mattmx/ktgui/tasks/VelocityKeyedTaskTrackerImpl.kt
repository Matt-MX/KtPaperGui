package com.mattmx.ktgui.tasks

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.scheduler.ScheduledTask
import kotlin.time.Duration

class VelocityKeyedTaskTrackerImpl(
    plugin: Any,
    proxyServer: ProxyServer
) : KeyedTaskTracker<ScheduledTask, Duration>() {
    private val provider = VelocityTaskProvider(plugin, proxyServer) { completedTask ->
        // TODO do by key
        cancelIf { _, scheduledTask -> scheduledTask == completedTask }
    }

    override fun cancel(task: ScheduledTask) {
        task.cancel()
    }

    override fun createTask(spec: TaskSpec<ScheduledTask, Duration>): ScheduledTask {
        return provider.createTask(spec)
    }
}