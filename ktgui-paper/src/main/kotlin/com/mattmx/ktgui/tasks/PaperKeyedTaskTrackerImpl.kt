package com.mattmx.ktgui.tasks

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.*
import net.kyori.adventure.key.Key
import org.bukkit.plugin.java.JavaPlugin

class PaperKeyedTaskTrackerImpl(
    private val plugin: JavaPlugin
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

    @OptIn(DelicateCoroutinesApi::class)
    fun coroutine(key: Key, scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> Unit): Job {
        val job = scope.launch { block() }
        val taskSpec = TaskSpec<PaperTaskWrapper>({ block(GlobalScope) }, true)

        this.tasks.put(key, PaperTaskWrapper(taskSpec, object : ScheduledTask {
            override fun getOwningPlugin() = this@PaperKeyedTaskTrackerImpl.plugin
            override fun isRepeatingTask() = taskSpec.isRepeating()
            override fun cancel(): ScheduledTask.CancelledState {
                job.cancel()
                return ScheduledTask.CancelledState.CANCELLED_BY_CALLER
            }

            override fun getExecutionState() = ScheduledTask.ExecutionState.RUNNING
        }))

        return job
    }
}