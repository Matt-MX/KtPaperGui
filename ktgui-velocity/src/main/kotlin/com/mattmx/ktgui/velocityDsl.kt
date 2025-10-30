package com.mattmx.ktgui

import com.mattmx.ktgui.impl.PacketEventsKtGui
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.tasks.VelocityKeyedTaskTrackerImpl
import com.mattmx.ktgui.tasks.VelocityTaskTrackerImpl
import com.velocitypowered.api.event.EventHandler
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import kotlin.reflect.KProperty

fun ProxyServer.taskTracker(plugin: Any) = VelocityTaskTrackerImpl(plugin, this)
fun ProxyServer.keyedTaskTracker(plugin: Any) = VelocityKeyedTaskTrackerImpl(plugin, this)

inline fun <reified E> GuiScreen<*, *>.onEventByPlayer(playerSupplier: KProperty<Player>, noinline callback: (E) -> Unit) {
    val plugin = VelocityKtGuiPlugin.getInstance()

    return onEvent<E>(plugin, plugin.proxyServer) { event ->
        val player = playerSupplier.getter.call(event)

        val isThisOpen = KtGui.getInstance<PacketEventsKtGui>().getActiveGui(player) == this

        if (isThisOpen) {
            callback(event)
        }
    }
}

inline fun <reified E> GuiScreen<*, *>.onEvent(noinline callback: (E) -> Unit) {
    val plugin = VelocityKtGuiPlugin.getInstance()

    return onEvent(plugin, plugin.proxyServer, callback)
}

inline fun <reified E> GuiScreen<*, *>.onEvent(plugin: Any, proxyServer: ProxyServer, noinline callback: (E) -> Unit) {
    val handler = EventHandler<E> { callback(it) }

    proxyServer.eventManager.register(plugin, E::class.java, handler)

    this.close {
        if (getAllWatchingInstance<Any>().isEmpty()) {
            proxyServer.eventManager.unregister(plugin, handler)
        }
    }
}