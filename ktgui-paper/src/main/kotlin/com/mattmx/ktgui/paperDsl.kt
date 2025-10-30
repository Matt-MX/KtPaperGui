package com.mattmx.ktgui

import com.mattmx.ktgui.event.PlayerClickButtonEvent
import com.mattmx.ktgui.impl.BukkitConvertedButton
import com.mattmx.ktgui.impl.PacketEventsKtGui
import com.mattmx.ktgui.impl.PacketGuiButton
import com.mattmx.ktgui.screen.GuiScreen
import com.mattmx.ktgui.screen.InventoryType
import com.mattmx.ktgui.tasks.PaperKeyedTaskTrackerImpl
import com.mattmx.ktgui.tasks.PaperTaskTrackerImpl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KFunction1

fun JavaPlugin.taskTracker(plugin: JavaPlugin) = PaperTaskTrackerImpl(plugin)
fun JavaPlugin.keyedTaskTracker(plugin: JavaPlugin) = PaperKeyedTaskTrackerImpl(plugin)

inline fun <reified E : Event> GuiScreen<*, *>.onEventByPlayer(
    playerSupplier: KFunction1<E, Player>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline callback: (E) -> Unit
) {
    return onEvent<E>(priority, ignoreCancelled) { event: E ->
        val player = playerSupplier.call(event)

        val isThisOpen = KtGui.getInstance<PacketEventsKtGui>().getActiveGui(player) == this

        if (isThisOpen) {
            callback(event)
        }
    }
}

inline fun <reified E : Event> GuiScreen<*, *>.onEvent(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline callback: (E) -> Unit
) {
    val plugin = PaperKtGuiPlugin.getInstance()

    return onEvent<E>(plugin, priority, ignoreCancelled, callback)
}

class KListener : Listener

inline fun <reified E : Event> GuiScreen<*, *>.onEvent(
    plugin: JavaPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline callback: (E) -> Unit
) {
    val listener = plugin.event<E>(priority, ignoreCancelled, callback)

    this.close {
        if (getWatchingInstance().isEmpty()) {
            HandlerList.unregisterAll(listener)
        }
    }
}

inline fun <reified E : Event> JavaPlugin.event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline callback: (E) -> Unit
): KListener {
    val handler = EventExecutor { _, event ->
        if (E::class.java.isInstance(event)) {
            callback(E::class.java.cast(event))
        }
    }

    val listener = KListener()

    Bukkit.getPluginManager().registerEvent(
        E::class.javaObjectType,
        listener,
        EventPriority.LOW,
        handler,
        this,
        true
    )

    return listener
}

val PlayerClickButtonEvent<*>.player: Player get() = getPlayer<Player>()

fun convertedButton(item: ItemStack, block: PacketGuiButton<*>.() -> Unit): BukkitConvertedButton<*> {
    return BukkitConvertedButton(item).apply(block)
}

val InventoryType.bukkit
    get() = org.bukkit.event.inventory.InventoryType
        .entries
        .firstOrNull { bukkitType -> bukkitType.menuType?.key()?.asString() == key }
        ?: error("No Bukkit type of InventoryType found for $this")