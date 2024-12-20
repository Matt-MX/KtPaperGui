package com.mattmx.ktgui.tasks

import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.scheduler.ScheduledTask
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class VelocityTaskProvider(
    private val plugin: Any,
    private val proxyServer: ProxyServer,
    private val after: (ScheduledTask) -> Unit
) : TaskProvider<ScheduledTask, Duration>() {
    override fun createTask(spec: TaskSpec<ScheduledTask, Duration>): ScheduledTask {
        return proxyServer.scheduler
            .buildTask(this.plugin) { task ->
                spec.callback.invoke(task)

                // If not a repeating task then we are done
                if (!spec.isRepeating()) {
                    after(task)
                }
            }
            .repeat(spec.period.orElse(Duration.ZERO).toJavaDuration())
            .delay(spec.delay.orElse(Duration.ZERO).toJavaDuration())
            .schedule()
    }
}