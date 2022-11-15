package com.mattmx.ktgui.dsl

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

class KListener<T : Event> : Listener

inline fun <reified T : Event> event(
    plugin: JavaPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) {
    val listener = KListener<T>()
    listener.event(plugin, priority, ignoreCancelled, block)
}

inline fun <reified T : Event> Listener.event(
    plugin: JavaPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) {
    event(plugin, T::class, priority, ignoreCancelled, block)
}

inline fun <T : Event> Listener.event(
    plugin: JavaPlugin,
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline callback: T.() -> Unit
) {
    Bukkit.getServer().pluginManager.registerEvent(
        type.java,
        this,
        priority,
        { _, event -> if(type.isInstance(event)) callback(event as T) },
        plugin,
        ignoreCancelled
    )
}
