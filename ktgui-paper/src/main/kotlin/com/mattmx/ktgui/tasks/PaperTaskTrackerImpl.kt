package com.mattmx.ktgui.tasks

import org.bukkit.plugin.java.JavaPlugin

class PaperTaskTrackerImpl(
    plugin: JavaPlugin
) : TaskTracker<PaperTaskWrapper>() {
    private val provider = PaperTaskProviderImpl(plugin) { task ->
        tasks.remove(task)
    }

    override fun createTask(spec: TaskSpec<PaperTaskWrapper>): PaperTaskWrapper {
        return provider.createTask(spec)
    }

    override fun cancel(task: PaperTaskWrapper) {
        task.cancel()
    }
}