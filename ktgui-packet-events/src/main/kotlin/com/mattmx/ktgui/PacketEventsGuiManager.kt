package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.item.type.ItemType
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes
import com.mattmx.ktgui.screen.GuiType
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import java.util.concurrent.atomic.AtomicInteger

abstract class PacketEventsGuiManager : GuiManager<Any, PacketGuiButton<*>, PacketGuiScreen<*>>() {
    private val windowIdCounter = AtomicInteger(1)
    private val listener = WindowEventsListener()

    fun getWindowId() : Int = windowIdCounter.getAndIncrement()

    fun registerListeners() {
        PacketEvents.getAPI()
            .eventManager
            .registerListener(listener)
    }

    fun unregisterListeners() {
        PacketEvents.getAPI()
            .eventManager
            .unregisterListener(listener)
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