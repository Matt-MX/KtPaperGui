package com.mattmx.ktgui.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.GuiManager
import com.mattmx.ktgui.listener.WindowEventsListener
import com.mattmx.ktgui.screen.GuiType
import com.mattmx.ktgui.listener.InventoryTracker
import com.mattmx.ktgui.listener.PlayerLocationTracker
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

abstract class PacketEventsGuiManager : GuiManager<Any, PacketGuiButton<*>, PacketGuiScreen<*>>() {
    private val windowIdCounter = AtomicInteger(1)
    private val listener = WindowEventsListener()
    val inventoryTracker = InventoryTracker()
    var playerLocationTracker = Optional.empty<PlayerLocationTracker>()

    fun getWindowId() : Int = windowIdCounter.getAndIncrement()

    open fun registerListeners() {
        val listeners = listOfNotNull(listener, inventoryTracker, playerLocationTracker.orElse(null))

        PacketEvents.getAPI()
            .eventManager
            .registerListeners(*listeners.toTypedArray())
    }

    open fun unregisterListeners() {
        val listeners = listOfNotNull(listener, inventoryTracker, playerLocationTracker.orElse(null))

        PacketEvents.getAPI()
            .eventManager
            .unregisterListeners(*listeners.toTypedArray())
    }

    fun trackPlayerLocations() = apply {
        if (this.playerLocationTracker.isPresent) return@apply
        this.playerLocationTracker = Optional.of(PlayerLocationTracker())
    }

    override fun createPlatformButtonOfType(typeKeyed: Key): PacketGuiButton<*> {
        val itemType = ItemTypes.getByName(typeKeyed.value()) ?: ItemTypes.AIR
        return PacketGuiButton(itemType)
    }

    override fun createPlatformButton(type: Any): PacketGuiButton<*> {
        val itemType = type as? ItemType ?: error("Type must be of ItemTypes")
        return PacketGuiButton(itemType)
    }

    override fun createPlatformGui(title: Component, type: GuiType): PacketGuiScreen<*> {
        return PacketGuiScreen(type, title)
    }
}