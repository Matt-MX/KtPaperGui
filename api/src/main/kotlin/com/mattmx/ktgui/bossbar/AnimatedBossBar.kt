package com.mattmx.ktgui.bossbar

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask

class AnimatedBossBar(var updateEvery: Long? = null) : BossBarBuilder() {
    private lateinit var task: BukkitTask
    var update: ((AnimatedBossBar) -> Unit)? = null

    fun update(builder: AnimatedBossBar.() -> Unit) : AnimatedBossBar {
        this.update = builder
        return this
    }

    fun begin(plugin: JavaPlugin) {
        updateEvery?.let {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, { task ->
                this.task = task
                update?.invoke(this)
            }, 0L, it)
        }
    }

}