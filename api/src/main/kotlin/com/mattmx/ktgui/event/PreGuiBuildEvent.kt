package com.mattmx.ktgui.event

import com.mattmx.ktgui.components.screen.IGuiScreen
import com.mattmx.ktgui.scheduling.isAsync
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PreGuiBuildEvent(
    val gui: IGuiScreen,
    val player: Player
) : Event(isAsync()), Cancellable {
    private var _cancelled: Boolean = false
    override fun getHandlers() = _handlers

    override fun isCancelled() = _cancelled

    override fun setCancelled(cancel: Boolean) {
        _cancelled = cancel
    }

    companion object {
        private val _handlers = HandlerList()

        @JvmStatic
        fun getHandlerList() = _handlers
    }
}