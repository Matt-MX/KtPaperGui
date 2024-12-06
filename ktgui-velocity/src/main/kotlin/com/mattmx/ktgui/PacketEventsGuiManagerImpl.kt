package com.mattmx.ktgui

import com.velocitypowered.api.proxy.ProxyServer
import java.time.Duration

class PacketEventsGuiManagerImpl(
    private val plugin: Any,
    private val proxy: ProxyServer
) : PacketEventsGuiManager() {

    init {
        setInstance(this)
    }

    override fun createRepeatingTask(repeat: Duration, task: () -> Unit): TaskWrapper {

        val scheduledTask = proxy.scheduler
            .buildTask(plugin, task)
            .repeat(repeat)
            .schedule()

        return TaskWrapper { scheduledTask.cancel() }
    }
}