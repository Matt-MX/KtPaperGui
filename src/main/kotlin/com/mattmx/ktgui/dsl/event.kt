package com.mattmx.ktgui.dsl

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

private class DummyListener<T>(
    private val e: T.() -> Unit
) : Listener where T : Event {
    @EventHandler
    fun onEvent(event: T) {
        e(event)
    }
}

fun <T> onEvent(plugin: JavaPlugin, e: T.() -> Unit) where T : Event {
    val listener = DummyListener(e)
    Bukkit.getPluginManager().registerEvents(listener, plugin)
}

