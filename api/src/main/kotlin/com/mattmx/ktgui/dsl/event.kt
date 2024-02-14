package com.mattmx.ktgui.dsl

import com.mattmx.ktgui.utils.JavaCompatibility
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class KListener : Listener

@JvmName("event1")
inline fun <reified T : Event> JavaPlugin.event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) {
    val listener = KListener()
    listener.event(this, priority, ignoreCancelled, block)
}
inline fun <reified T : Event> event(
    plugin: JavaPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) {
    val listener = KListener()
    listener.event(plugin, priority, ignoreCancelled, block)
}

@JavaCompatibility
fun <T : Event> javaEvent(
    plugin: JavaPlugin,
    clazz: Class<T>,
    block: T.() -> Unit
) {
    javaEvent(plugin, clazz, EventPriority.NORMAL, false, block)
}

@JavaCompatibility
fun <T : Event> javaEvent(
    plugin: JavaPlugin,
    clazz: Class<T>,
    priority: EventPriority,
    block: T.() -> Unit
) {
    javaEvent(plugin, clazz, priority, false, block)
}

@JavaCompatibility
fun <T : Event> javaEvent(
    plugin: JavaPlugin,
    clazz: Class<T>,
    ignoreCancelled: Boolean,
    block: T.() -> Unit
) {
    javaEvent(plugin, clazz, EventPriority.NORMAL, ignoreCancelled, block)
}

@JavaCompatibility
fun <T : Event> javaEvent(
    plugin: JavaPlugin,
    clazz: Class<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    block: T.() -> Unit
) {
    val listener = KListener()
    listener.event(plugin, clazz, priority, ignoreCancelled, block)
}

inline fun <reified T : Event> Listener.event(
    plugin: JavaPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) {
    event(plugin, T::class.java, priority, ignoreCancelled, block)
}

inline fun <T : Event> Listener.event(
    plugin: JavaPlugin,
    type: Class<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline callback: T.() -> Unit
) {
    Bukkit.getServer().pluginManager.registerEvent(
        type,
        this,
        priority,
        { _, event -> if(type.isInstance(event)) callback(event as T) },
        plugin,
        ignoreCancelled
    )
}