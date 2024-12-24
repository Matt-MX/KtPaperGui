package com.mattmx.ktgui.tasks

import org.bukkit.plugin.java.JavaPlugin

class PaperKeyedTaskTrackerImpl(
    plugin: JavaPlugin
) : KeyedTaskTracker<PaperTaskWrapper>() {
    private val provider = PaperTaskProviderImpl(plugin) { task ->
        // TODO do by key
        cancelIf { _, paperTaskWrapper -> paperTaskWrapper == task }
    }

    override fun cancel(task: PaperTaskWrapper) {
        task.cancel()
    }

    override fun createTask(spec: TaskSpec<PaperTaskWrapper>): PaperTaskWrapper {
        return provider.createTask(spec)
    }
}