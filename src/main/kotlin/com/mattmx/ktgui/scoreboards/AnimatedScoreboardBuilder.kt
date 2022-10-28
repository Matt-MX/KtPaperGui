package com.mattmx.ktgui.scoreboards

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

open class AnimatedScoreboardBuilder(
    title: String,
    val updateEvery: Long = 2,
) : ScoreboardBuilder(title) {
    var update: ((AnimatedScoreboardBuilder) -> Unit)? = null
    private var cancel = false
    private var started: Long = 0L
    private var iterations: Long = 0L

    /**
     * Starts the animation loop. Call this when you are ready to start
     * animating.
     * The method will call the update callback every updateEvery ticks.
     *
     * @param plugin the plugin instance
     */
    fun begin(plugin: JavaPlugin) : AnimatedScoreboardBuilder {
        started = System.currentTimeMillis()
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, { it ->
            if (!cancel) update?.invoke(this)
            else {
                it.cancel()
            }
            iterations++
        } ,0, updateEvery)
        return this
    }

    /**
     * @return the number of iterations we have been through.
     */
    fun getIterations() : Long {
        return iterations
    }

    /**
     * @return the time (in millis) that we have been running this scoreboard for.
     */
    fun getTimeRunning() : Long {
        return System.currentTimeMillis() - started
    }

    /**
     * If you want to stop animating the scoreboard, call this.
     * To begin animating again, call begin(plugin)
     */
    fun cancel() : AnimatedScoreboardBuilder {
        cancel = true
        return this
    }

    /**
     * Method to set the update callback for the animated scoreboard.
     *
     * @param scoreboard the callback of how we want the scoreboard to update.
     */
    fun update(scoreboard: AnimatedScoreboardBuilder.() -> Unit) : AnimatedScoreboardBuilder {
        this.update = scoreboard
        return this
    }

}