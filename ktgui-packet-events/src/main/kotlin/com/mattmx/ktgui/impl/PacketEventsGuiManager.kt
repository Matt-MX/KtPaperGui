package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow
import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.listener.WindowEventsListener
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.listener.InventoryTracker
import com.mattmx.ktgui.listener.PlayerLocationTracker
import com.mattmx.ktgui.listener.PlayerLocationTrackerImpl
import com.mattmx.ktgui.screen.InventoryTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

abstract class PacketEventsGuiManager : GuiManager<Any, PacketGuiButton<*>, PacketGuiInventoryScreen<*>>() {
    private val windowIdCounter = AtomicInteger(1)
    private val listener = WindowEventsListener()
    open val inventoryTracker = InventoryTracker()
    open var playerLocationTracker = Optional.empty<PlayerLocationTracker>()

    fun getWindowId() : Int = windowIdCounter.getAndIncrement()

    open fun registerListeners() {
        val listeners = listOfNotNull(listener, inventoryTracker, playerLocationTracker.orElse(null))
            .filterIsInstance<PacketListenerAbstract>()

        PacketEvents.getAPI()
            .eventManager
            .registerListeners(*listeners.toTypedArray())
    }

    open fun unregisterListeners() {
        val listeners = listOfNotNull(listener, inventoryTracker, playerLocationTracker.orElse(null))
            .filterIsInstance<PacketListenerAbstract>()

        PacketEvents.getAPI()
            .eventManager
            .unregisterListeners(*listeners.toTypedArray())
    }

    override fun forcefullyClose(player: Any) {
        val gui = removeActiveGui(player) ?: return

        gui.close.apply(player)

        val packet = WrapperPlayServerCloseWindow(gui.windowId)

        PacketEvents.getAPI()
            .playerManager
            .sendPacket(player, packet)
    }

    fun withDefaultLocationTracker() = apply {
        if (this.playerLocationTracker.isPresent) return@apply
        this.playerLocationTracker = Optional.of(PlayerLocationTrackerImpl())
    }

    override fun createPlatformButtonOfType(typeKeyed: Key): PacketGuiButton<*> {
        val itemType = ItemTypes.getByName(typeKeyed.value()) ?: ItemTypes.AIR
        return PacketGuiButton(itemType)
    }

    override fun createPlatformButton(type: Any): PacketGuiButton<*> {
        val itemType = type as? ItemType ?: error("Type must be of ItemTypes")
        return PacketGuiButton(itemType)
    }

    override fun createPlatformGui(title: Component, type: GuiType): PacketGuiInventoryScreen<*> {
        if (type.type == InventoryTypes.CUSTOM_HOTBAR) {
//            return PacketGuiHotbarScreen()
        }

        return PacketGuiInventoryScreen(type, title)
    }
}