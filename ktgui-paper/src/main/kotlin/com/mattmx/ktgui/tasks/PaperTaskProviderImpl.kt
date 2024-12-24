package com.mattmx.ktgui.tasks

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PaperTaskProviderImpl(
    private val plugin: JavaPlugin,
    private val after: (PaperTaskWrapper) -> Unit
) : TaskProvider<PaperTaskWrapper>() {
    override fun createTask(spec: TaskSpec<PaperTaskWrapper>): PaperTaskWrapper {

        var wrapper: PaperTaskWrapper? = null

        val stupidCallback: (ScheduledTask) -> Unit = { task ->
            spec.callback.invoke(wrapper!!)

            if (!spec.isRepeating()) {
                after(wrapper!!)
            }
        }

        val scheduledTask: ScheduledTask = if (spec.async) {
            val scheduler = Bukkit.getAsyncScheduler()

            if (spec.period.isPresent) scheduler.runAtFixedRate(
                plugin,
                stupidCallback,
                spec.period.get().inWholeMilliseconds,
                spec.delay.orElse(0.seconds).inWholeMilliseconds,
                TimeUnit.MILLISECONDS
            )
            else if (spec.delay.isPresent) scheduler.runDelayed(plugin, stupidCallback, spec.delay.get().inWholeMilliseconds, TimeUnit.MILLISECONDS)
            else scheduler.runNow(plugin, stupidCallback)
        } else {
            val scheduler = Bukkit.getGlobalRegionScheduler()

            if (spec.period.isPresent) scheduler.runAtFixedRate(
                plugin,
                stupidCallback,
                spec.period.get().ticks,
                spec.delay.orElse(0.seconds).ticks
            )
            else if (spec.delay.isPresent) scheduler.runDelayed(plugin, stupidCallback, spec.delay.get().ticks)
            else scheduler.run(plugin, stupidCallback)
        }

        wrapper = PaperTaskWrapper(spec, scheduledTask)

        return wrapper
    }

    private val Duration.ticks
        get() = inWholeMilliseconds / 50L
}