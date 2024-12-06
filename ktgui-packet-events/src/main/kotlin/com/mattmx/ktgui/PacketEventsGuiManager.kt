package com.mattmx.ktgui

import com.github.retrooper.packetevents.PacketEvents
import java.util.concurrent.atomic.AtomicInteger

abstract class PacketEventsGuiManager : GuiManager<Any, PacketGuiButton<*>, PacketGuiScreen<*>>() {
    private val windowIdCounter = AtomicInteger(-1)
    private val listener = WindowEventsListener()

    fun getWindowId() : Int = windowIdCounter.getAndDecrement()

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

    companion object {
        lateinit var instance: PacketEventsGuiManager
    }
}