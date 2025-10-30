package com.mattmx.ktgui.tasks

import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class VelocityTaskProvider(
    private val plugin: Any,
    private val proxyServer: ProxyServer,
    private val completedCallback: (VelocityTaskWrapper) -> Unit
) : TaskProvider<VelocityTaskWrapper>() {
    override fun createTask(spec: TaskSpec<VelocityTaskWrapper>): VelocityTaskWrapper {
        val wrapper = VelocityTaskWrapper(spec)

        wrapper.instance = proxyServer.scheduler
            .buildTask(this.plugin) { task ->
                runBlocking {
                    spec.callback.invoke(wrapper)
                }

                // If not a repeating task then we are done
                if (!spec.isRepeating()) {
                    completedCallback(wrapper)
                }
            }
            .repeat(spec.period.orElse(Duration.ZERO).toJavaDuration())
            .delay(spec.delay.orElse(Duration.ZERO).toJavaDuration())
            .schedule()

        return wrapper
    }
}