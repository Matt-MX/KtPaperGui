package com.mattmx.ktgui.tasks

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import kotlinx.coroutines.*
import org.bukkit.plugin.java.JavaPlugin

class PaperTaskTrackerImpl(
    private val plugin: JavaPlugin
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

    @OptIn(DelicateCoroutinesApi::class)
    fun loop(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> Unit): Job {
        return coroutine(scope) {
            while (true) {
                block()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun coroutine(scope: CoroutineScope = GlobalScope, block: suspend CoroutineScope.() -> Unit): Job {
        val job = scope.launch { block() }
        val taskSpec = TaskSpec<PaperTaskWrapper>({ block(GlobalScope) }, true)

        this.tasks.add(PaperTaskWrapper(taskSpec, object : ScheduledTask {
            override fun getOwningPlugin() = this@PaperTaskTrackerImpl.plugin
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